package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
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
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String bucket;
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final ApplicationEventPublisher eventPublisher;

  // 생성자를 통한 의존성 주입 및 S3 클라이언트 초기화
  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket,
      ApplicationEventPublisher eventPublisher) {

    this.bucket = bucket;
    this.eventPublisher = eventPublisher;

    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
    Region awsRegion = Region.of(region);

    // Client와 Presigner는 생성 비용이 비싸므로 여기서 한 번만 생성해서 재사용
    this.s3Client = S3Client.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(awsRegion)
        .credentialsProvider(credentialsProvider)
        .build();

    log.info("S3 저장소가 초기화되었습니다. 버킷: {}", bucket);
  }

  // 클라이언트가 업로드한 파일을 S3 버킷에 저장하는 함수
  @Retryable( // 파일 업로드 실패 시 메커니즘
      retryFor = S3Exception.class,
      maxAttempts = 3, // 최대 재시도 횟수
      backoff = @Backoff(delay = 1000, multiplier = 2) // 재시도 인터벌 설정
  )
  @Override
  public UUID put(UUID id, byte[] data) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build();

    getS3Client().putObject(putObjectRequest, RequestBody.fromBytes(data));
    return id;
  }

  // 파일 업로드 재시도가 모두 실패했을 때 실행되는 함수
  @Recover
  public UUID recover(S3Exception e, UUID binaryContentId, byte[] bytes) {
    log.error("S3 업로드 재시도 실패: {}, key={}", e.getMessage(), binaryContentId);

    // 현재 스레드에 보존되어 있는 Request ID 추출
    String requestId = MDC.get(MDCLoggingInterceptor.REQUEST_ID);
    if (requestId == null) {
      requestId = "UNKNOWN";
    }

    // 이벤트를 발행하여 다른 계층으로 에러 전파
    eventPublisher.publishEvent(new S3UploadFailedEvent(binaryContentId, e, requestId));

    throw new RuntimeException("S3 파일 저장에 최종 실패했습니다.", e);
  }

  // S3에서 서버로 파일 가져오는 함수
  @Override
  public InputStream get(UUID id) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(id.toString())
        .build();

    // ResponseInputStream은 InputStream을 상속하므로 바로 리턴 가능
    return getS3Client().getObject(getObjectRequest);
  }

  // 클라이언트에게 다운로드 링크 제공하는 함수
  @Override
  public ResponseEntity<Void> download(BinaryContentDto dto) {
    // 1. S3 Presigned URL 생성
    String presignedUrl = generatePresignedUrl(dto.id().toString(), dto.contentType());

    // 2. 클라이언트가 S3에서 직접 다운로드하도록 302 Redirect 응답 반환
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  // --- Helper Methods ---

  private S3Client getS3Client() {
    return this.s3Client;
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType) // 브라우저가 다운로드 시 올바른 타입으로 인식하도록 설정
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분 후 만료
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    return presignedRequest.url().toString();
  }
}