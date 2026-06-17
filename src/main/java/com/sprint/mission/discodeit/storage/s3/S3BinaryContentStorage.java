package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.exception.s3.S3DownloadFailException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final ApplicationEventPublisher eventPublisher;
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final long presignedUrlExpiration;

  public S3BinaryContentStorage(
      ApplicationEventPublisher eventPublisher,
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpiration) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;
    this.presignedUrlExpiration = presignedUrlExpiration;
    this.eventPublisher = eventPublisher;

    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(getCredentialsProvider())
        .build();

    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(getCredentialsProvider())
        .build();
  }


  @Retryable(
      retryFor = RuntimeException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2.0)
  )
  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    String key = binaryContentId.toString();
    s3Client.putObject(req -> req.bucket(bucket).key(key), RequestBody.fromBytes(bytes));
    log.info("[Storage] 파일 저장 성공: {}", binaryContentId);
    return binaryContentId;
  }

  @Recover
  public UUID recover(RuntimeException e, UUID binaryContentId, byte[] bytes) {
    String requestId = MDC.get("requestId");
    String error = String.format("RequestId: %s\nBinaryContentId: %s\nError: %s",
        requestId, binaryContentId, e.getMessage());

    eventPublisher.publishEvent(
        new BinaryContentUploadFailedEvent(requestId, binaryContentId.toString(), error));
    throw e;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    String key = binaryContentId.toString();

    try {
      byte[] bytes = s3Client.getObjectAsBytes(req -> req.bucket(bucket).key(key)).asByteArray();

      return new ByteArrayInputStream(bytes);
    } catch (RuntimeException e) {
      throw new S3DownloadFailException(e);
    }
  }

  @Override
  public void delete(UUID binaryContentId) {
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String presignedUrl = generatePresignedUrl(dto.id().toString(), dto.contentType());
    log.info("[Storage] presignedUrl 발급 성공: {}", presignedUrl);

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
        req -> req.getObjectRequest(request)
            .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))); // 유효기간(단위: 초))

    return presignedRequest.url().toString();
  }


  private AwsCredentialsProvider getCredentialsProvider() {
    return this.accessKey != null && !this.accessKey.isBlank()
        // 수동탐색 방식: (.env, yaml 설정파일 사용)
        ? StaticCredentialsProvider.create(
        AwsBasicCredentials.create(this.accessKey, this.secretKey))
        // 자동탐색 방식: (Java 시스템 속성 -> 환경 변수 -> 자격 증명 파일(AWS CLI 설정값) -> 컨테이너/EC2(IAM ROLE))
        : DefaultCredentialsProvider.create();
  }
}
