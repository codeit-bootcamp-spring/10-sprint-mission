package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  @Value("${discodeit.storage.s3.access-key}")
  private String accessKey;

  @Value("${discodeit.storage.s3.secret-key}")
  private String secretKey;

  @Value("${discodeit.storage.s3.region}")
  private String region;

  @Value("${discodeit.storage.s3.bucket}")
  private String bucket;

  @Value("${discodeit.storage.s3.presigned-url-expiration:600}") // 기본값 10분
  private long presignedUrlExpirationSeconds;

  @Retryable(retryFor = { S3Exception.class,
      RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = binaryContentId.toString();
    try {
      S3Client s3Client = getS3Client();

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      s3Client.putObject(request, RequestBody.fromBytes(bytes));
      log.info("S3에 파일 업로드 성공: {}", key);

      return binaryContentId;
    } catch (S3Exception e) {
      log.error("S3에 파일 업로드 실패: {}", e.getMessage());
      throw e;
    }
  }

  @Recover
  public UUID recover(Exception e, UUID binaryContentId, byte[] bytes) {
    String requestId = MDC.get("requestId");
    log.error("S3 업로드 재시도가 모두 실패하여 복구 로직을 실행합니다. requestId={}, binaryContentId={}", requestId, binaryContentId,
        e);

    List<User> admins = userRepository.findAllByRole(Role.ADMIN);
    if (admins.isEmpty()) {
      log.warn("알림을 보낼 ADMIN 사용자가 존재하지 않습니다.");
      return binaryContentId;
    }

    String title = "S3 파일 업로드 실패";
    String content = String.format(
        "RequestId: %s\nBinaryContentId: %s\nError: %s",
        requestId != null ? requestId : "N/A",
        binaryContentId,
        e.getMessage());

    for (User admin : admins) {
      Notification notification = new Notification(admin, title, content);
      notificationRepository.save(notification);
      log.info("ADMIN 관리자({})에게 S3 업로드 실패 알림을 등록했습니다.", admin.getUsername());
    }

    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    String key = binaryContentId.toString();
    try {
      S3Client s3Client = getS3Client();

      GetObjectRequest request = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      byte[] bytes = s3Client.getObjectAsBytes(request).asByteArray();
      return new ByteArrayInputStream(bytes);
    } catch (S3Exception e) {
      log.error("S3에서 파일 다운로드 실패: {}", e.getMessage());
      throw new NoSuchElementException("File with key " + key + " does not exist");
    }
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto metaData) {
    try {
      String key = metaData.id().toString();
      String presignedUrl = generatePresignedUrl(key, metaData.contentType());

      log.info("생성된 Presigned URL: {}", presignedUrl);

      return ResponseEntity
          .status(HttpStatus.FOUND)
          .header(HttpHeaders.LOCATION, presignedUrl)
          .build();
    } catch (Exception e) {
      log.error("Presigned URL 생성 실패: {}", e.getMessage());
      throw new RuntimeException("Presigned URL 생성 실패", e);
    }
  }

  private String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = getS3Presigner()) {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .responseContentType(contentType)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofSeconds(presignedUrlExpirationSeconds))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
      return presignedRequest.url().toString();
    }
  }

  private S3Presigner getS3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
}