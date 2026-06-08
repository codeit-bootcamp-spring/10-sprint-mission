package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.event.BinaryUploadFailedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.storage.s3.config.Properties;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final ApplicationEventPublisher eventPublisher;

  // 다이어그램 명시 생성자
  public S3BinaryContentStorage(Properties props, ApplicationEventPublisher eventPublisher) {
    this.accessKey = props.getCredentials().getAccessKey();
    this.secretKey = props.getCredentials().getSecretKey();
    this.region = props.getRegion();
    this.bucket = props.getS3().getBucket();
    this.eventPublisher = eventPublisher;
  }

  @Override
  @Retryable(
      retryFor = Exception.class,
      maxAttempts = 3,
      backoff = @Backoff(
          delay = 1000,
          multiplier = 2
      )
  )
  public UUID put(UUID id, byte[] content) {
    log.warn("[RETRY TEST] S3 Upload Attempt: {}", id);

    PutObjectRequest putOb = PutObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build();

    getS3Client().putObject(putOb, RequestBody.fromBytes(content));
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    GetObjectRequest getOb = GetObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build();

    return getS3Client().getObject(getOb);
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    String signedUrl = generatePresignedUrl(dto.getId().toString(), "application/octet-stream");

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(signedUrl))
        .build();
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build()) {

      GetObjectRequest getRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(getRequest)
          .build();

      return presigner.presignGetObject(presignRequest).url().toString();
    }
  }

  @Recover
  public UUID recover(Exception e, UUID id, byte[] content) {
    String requestId = MDC.get("requestId");

    eventPublisher.publishEvent(
        new BinaryUploadFailedEvent(
            id,
            requestId,
            e.getMessage()
        )
    );

    throw new DiscodeitException(ErrorCode.BINARY_UPLOAD_FAILED);
  }
}
