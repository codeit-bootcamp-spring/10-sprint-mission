package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
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
    private final long presignedUrlExpiration;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration:600}") long presignedUrlExpiration,
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        log.debug("S3 파일 업로드 시도: id={}", binaryContentId);
        String key = resolveKey(binaryContentId);

        try (S3Client s3 = getS3Client()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3.putObject(request, RequestBody.fromBytes(bytes));
        }

        return binaryContentId;
    }

    @Recover
    public UUID recover(Exception e, UUID binaryContentId, byte[] bytes) {
        String requestId = String.valueOf(MDC.get(MDCLoggingInterceptor.REQUEST_ID));

        log.error("S3 파일 업로드 최종 실패: id={}, requestId={}", binaryContentId, requestId, e);

        String title = "S3 파일 업로드 실패";
        String content = String.format(
                "RequestId: %s\nBinaryContentId: %s\nError: %s",
                requestId,
                binaryContentId,
                e.getMessage()
        );

        userRepository.findAllByRole(Role.ADMIN).forEach(admin ->
                notificationRepository.save(new Notification(admin, title, content))
        );

        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(resolveKey(binaryContentId))
                .build();

        return getS3Client().getObject(request);
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto metaData) {
        String key = resolveKey(metaData.id());
        String presignedUrl = generatePresignedUrl(key, metaData.contentType());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }

    // private

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

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentType(contentType)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
            return presigned.url().toString();
        }
    }

    private String resolveKey(UUID binaryContentId) {
        return "binary-content/" + binaryContentId;
    }
}