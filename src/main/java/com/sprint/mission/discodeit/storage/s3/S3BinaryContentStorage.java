package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@Component
public class S3BinaryContentStorage implements BinaryContentStorage {

    private static final String OPERATION_PUT = "S3 파일 업로드";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;
    private final Duration presignedUrlExpiration;
    private final ApplicationEventPublisher eventPublisher;

    public S3BinaryContentStorage(S3Properties properties,
        ApplicationEventPublisher eventPublisher) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.accessKey(),
                properties.secretKey()
        );

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                credentials);
        Region region = Region.of(properties.region());

        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        this.s3Presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        this.bucket = properties.bucket();
        this.presignedUrlExpiration = Duration.ofSeconds(properties.presignedUrlExpiration());
        this.eventPublisher = eventPublisher;
    }

    @Retryable(
        retryFor = SdkException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        log.debug("S3 업로드 시도: binaryContentId={}", binaryContentId);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(binaryContentId.toString())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return binaryContentId;
    }

    @Recover
    public UUID recoverPut(SdkException e, UUID binaryContentId, byte[] bytes) {
        String requestId = MDC.get(MDCLoggingInterceptor.REQUEST_ID);
        log.error("S3 업로드 재시도 모두 실패: binaryContentId={}, requestId={}",
            binaryContentId, requestId, e);

        eventPublisher.publishEvent(new S3UploadFailedEvent(
            binaryContentId,
            requestId,
            OPERATION_PUT,
            e.getMessage()
        ));

        throw new BinaryContentUploadFailedException(binaryContentId, e);
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(binaryContentId.toString())
                .build();

        return s3Client.getObject(request);
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto metaData) {
        String presignedUrl = generatePresignedUrl(metaData.id().toString());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }

    private String generatePresignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(presignedUrlExpiration)
                .getObjectRequest(r -> r.bucket(bucket).key(key))
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
        return presigned.url().toString();
    }

    public static class BinaryContentUploadFailedException extends RuntimeException {
        public BinaryContentUploadFailedException(UUID binaryContentId, Throwable cause) {
            super("S3 업로드에 최종 실패했습니다: binaryContentId=" + binaryContentId, cause);
        }
    }
}
