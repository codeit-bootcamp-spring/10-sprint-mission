package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.aws.AwsProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.AwsServerConnectFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentReadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.PresignedUrlCreateFailedException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final AwsProperties awsProperties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Retryable(
            retryFor = {
                    BinaryContentSaveFailedException.class,
                    AwsServerConnectFailedException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            recover = "putFallback"
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String key = resolveKey(binaryContentId);

        try {
            // 어떤 파일을 업로드할지 담는 요청 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsProperties.getBucket())  // 어느 bucket에 저장할 건지
                    .key(key) // 어떤 이름으로 저장할 건지
                    .build();

            // 생성한 요청 객체를 바탕으로 업로드(저장)
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        } catch (S3Exception e) {
            throw new BinaryContentSaveFailedException(binaryContentId, e);
        } catch (SdkClientException e) {
            throw new AwsServerConnectFailedException(binaryContentId, e);
        }

        return binaryContentId;
    }

    // BinaryContentSaveFailedException 예외 발생 시 실행되는 복구 메서드
    @Recover
    public UUID putFallback(BinaryContentSaveFailedException e, UUID binaryContentId) {
        log.error("[S3_UPLOAD_FALLBACK] S3 업로드 Fallback 실행: errorMessage={}",
                e.getMessage(), e);

        applicationEventPublisher.publishEvent(
                new S3UploadFailedEvent(
                        binaryContentId,
                        e
                )
        );

        // binaryContent 상태를 Fail로 변경시키기 위해서는 예외를 던져야 함.
        throw e;
    }

    // AwsServerConnectFailedException 예외 발생 시 실행되는 복구 메서드
    @Recover
    public UUID putFallback(AwsServerConnectFailedException e, UUID binaryContentId) {
        log.error("[S3_UPLOAD_FALLBACK] S3 업로드 Fallback 실행: errorMessage={}",
                e.getMessage(), e);

        applicationEventPublisher.publishEvent(
                new S3UploadFailedEvent(
                        binaryContentId,
                        e
                )
        );

        // binaryContent 상태를 Fail로 변경시키기 위해서는 예외를 던져야 함.
        throw e;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String key = resolveKey(binaryContentId);

        try {
            // 어떤 파일을 가져올지 담는 요청 객체 생성
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(awsProperties.getBucket())
                    .key(key)
                    .build();

            // 생성한 요청 객체를 바탕으로 S3에 파일을 요청
            return s3Client.getObject(getObjectRequest);
        } catch (S3Exception e) {
            throw new BinaryContentReadFailedException(binaryContentId, e);
        } catch (SdkClientException e) {
            throw new AwsServerConnectFailedException(binaryContentId, e);
        }
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto binaryContentDto) {
        // InputStream :  바이트 데이터를 읽기 위한 통로
        String key = resolveKey(binaryContentDto.id());

        try {
            String signedUrl = generatePresignedUrl(key, binaryContentDto.contentType());

            return ResponseEntity.status(HttpStatus.FOUND) // 리다이렉트 -> 302 FOUND
                    .location(URI.create(signedUrl))
                    .build();
        } catch (S3Exception e) { // S3에서 거부
            throw new PresignedUrlCreateFailedException(binaryContentDto.id(), e);
        } catch (SdkClientException e) { // AWS 연결 불가
            throw new AwsServerConnectFailedException(binaryContentDto.id(), e);
        }
    }

    private String resolveKey(UUID binaryContentId) {
        return binaryContentId.toString();
    }

    private String generatePresignedUrl(String key, String contentType) {
        // 어떤 파일을 가져올지 담는 요청 객체 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .responseContentType(contentType)
                .responseContentDisposition("attachment")
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofSeconds(awsProperties.getPresignedUrlExpiration()))
                .build();

        return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }
}
