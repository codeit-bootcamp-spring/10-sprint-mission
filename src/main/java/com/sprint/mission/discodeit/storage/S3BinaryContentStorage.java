package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    // 싱글톤 패턴 적용
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final ApplicationEventPublisher applicationEventPublisher;

    public S3BinaryContentStorage(@Value("${discodeit.storage.s3.access-key}") String accessKey,
                                  @Value("${discodeit.storage.s3.secret-key}") String secretKey,
                                  @Value("${discodeit.storage.s3.region}") String region,
                                  @Value("${discodeit.storage.s3.bucket}") String bucket,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.applicationEventPublisher = applicationEventPublisher;

        // AWS 출입증
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // AWS 요청 객체
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        // Presigned URL 객체 요청
        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    // 첨부 파일 저장 (재시도 포함)
    @Retryable(
            retryFor = { RuntimeException.class },                  // 재시도할 예외
            maxAttempts = 3,                                        // 최대 시도 횟수 (기본값 3)
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        // 첨부 파일 업로드 요청 객체
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(binaryContentId.toString())
                .build();

        // 첨부 파일 업로드
        s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));

        return binaryContentId;
    }

    // 재시도가 모두 실패한 경우 실행되는 복구 메서드
    @Recover
    public UUID recover(RuntimeException e, UUID binaryContentId, byte[] bytes) {
        // 메인 스레드의 requestId 추출
        String requestId = MDC.get("requestId") != null
                ? MDC.get("requestId")
                : "UNKNOWN_REQUEST_ID";

        log.error("[S3 UPLOAD] Failed S3 Upload. RequestId: {}, Error: {}",requestId, e.getMessage());

        applicationEventPublisher.publishEvent(new S3UploadFailedEvent(
                binaryContentId,
                requestId,
                e.getMessage()
        ));

        return binaryContentId;
    }

    // 첨부 파일 -> InputStream으로 변환
    @Override
    public InputStream get(UUID binaryContentId) {
        // 첨부 파일 변환 요청 객체
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(binaryContentId.toString())
                .build();

        return s3Client.getObject(getRequest);
    }

    // 첨부 파일 다운로드
    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        // 다운로드를 위한 임시 URL
        String presignedUrl = generatePresignedUrl(binaryContentDto.id().toString(), binaryContentDto.contentType());

        // 다운로드 페이지 연결
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + binaryContentDto.fileName() + "\"")
                .build();
    }

    // S3Client (AWS 요청 객체) 생성
    private S3Client getS3Client() {
        return this.s3Client;
    }

    // PresignedURL 생성
    private String generatePresignedUrl(String key, String contentType) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentType(contentType)
                .build();

        // 다운로드 요청 객체 -> URL 변환 요청 객체 변환
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))          // 유효시간 10분
                .getObjectRequest(getObjectRequest)
                .build();

        // URL 반환
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
