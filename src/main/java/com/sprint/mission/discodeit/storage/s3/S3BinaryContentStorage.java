package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.S3Properties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentDownloadException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentUploadException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Slf4j
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Properties s3Properties;
  private final ApplicationEventPublisher eventPublisher;
  private S3Client s3Client;
  private S3Presigner s3Presigner;

  // S3key는 현재 코드 구조 상 UUID를 사용

  @PostConstruct
  public void init() {
    log.info("[BINARY_CONTENT] S3 스토리지 활성화: 버킷명 = {}", s3Properties.getBucket());
    this.s3Client = getS3Client();
    this.s3Presigner = getS3Presigner();
  }

  @Retryable(
      retryFor = BinaryContentUploadException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  @Override
  public UUID put(UUID id, byte[] bytes) {
    log.info("[BINARY_CONTENT] S3 파일 업로드 시작 binaryContentId={}", id);
    try {
      String contentType = URLConnection.guessContentTypeFromStream(
          new ByteArrayInputStream(bytes));
      if (contentType == null) {
        contentType = "application/octet-stream"; // 알 수 없을 때 기본값
      }
      String key = id.toString();
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(s3Properties.getBucket())
          .key(key)
          .contentType(contentType)
          .build();
      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
      return id;
    } catch (Exception e) {
      throw new BinaryContentUploadException(e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      String key = id.toString();
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(s3Properties.getBucket())
          .key(key)
          .build();

      return s3Client.getObject(getObjectRequest);
    } catch (S3Exception e) {
      throw new BinaryContentDownloadException();
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    String signed = generatePresignedUrl(dto.id().toString(), dto.contentType(), dto.fileName());
    return ResponseEntity.status(302).location(URI.create(signed)).build();
  }

  private S3Client getS3Client() {
    if (s3Properties.getAccessKey() != null && !s3Properties.getAccessKey().isBlank()) {
      return S3Client.builder()
          .region(Region.of(s3Properties.getRegion()))
          .credentialsProvider(StaticCredentialsProvider.create(
                  AwsBasicCredentials.create(
                      s3Properties.getAccessKey(),
                      s3Properties.getSecretKey()
                  )
              )
          ).build();
    }
    return S3Client.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  private S3Presigner getS3Presigner() {
    if (s3Properties.getAccessKey() != null && !s3Properties.getAccessKey().isBlank()) {
      return S3Presigner.builder()
          .region(Region.of(s3Properties.getRegion()))
          .credentialsProvider(StaticCredentialsProvider.create(
                  AwsBasicCredentials.create(
                      s3Properties.getAccessKey(),
                      s3Properties.getSecretKey()
                  )
              )
          ).build();
    }
    return S3Presigner.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  private String generatePresignedUrl(String key, String contentType, String fileName) {
    String name = (fileName != null && !fileName.isBlank())
        ? fileName
        : Paths.get(key).getFileName().toString();

    String encodedFileName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
    String contentDisposition =
        "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName;

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(s3Properties.getBucket())
        .key(key)
        .responseContentType(contentType)
        .responseContentDisposition(contentDisposition)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(getObjectRequest)
        .signatureDuration(Duration.ofMinutes(5))
        .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }

  @Recover
  public UUID recover(BinaryContentUploadException e, UUID id, byte[] bytes) {
    log.info("[BINARY_CONTENT] S3 파일 업로드 Recover 메서드 진입");
    String requestId = MDC.get("request_id");
    String errorMessage = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
    eventPublisher.publishEvent(new BinaryContentUploadFailedEvent(requestId, id, errorMessage));
    log.error("[BINARY_CONTENT] S3 파일 업로드 실패");
    throw e;
  }
}
