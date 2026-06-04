package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentException;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@ConditionalOnProperty(
        prefix = "discodeit.storage",
        name = "type",
        havingValue = "s3"
)
@RequiredArgsConstructor
@Slf4j
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3StorageProperties props;
    private final NotificationService notificationService;

    @Retryable(
            retryFor = BinaryContentException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        String key = binaryContentId.toString();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromBytes(bytes));
            log.info("[BINARYCONTENT_S3_SAVE_SUCCESS] S3 파일 저장 성공: binaryContentId={}, bucket={}, key={}",
                    binaryContentId, props.getBucket(), key);
            return binaryContentId;
        } catch (S3Exception e) {
            log.error("[BINARYCONTENT_S3_SAVE_FAIL] S3 파일 저장 실패: binaryContentId={}, bucket={}, key={}",
                    binaryContentId, props.getBucket(), key, e);
            throw new BinaryContentException(
                    ErrorCode.BINARY_CONTENT_CAN_NOT_SAVE,
                    Map.of("binaryContentId", binaryContentId)
            );
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        String key = binaryContentId.toString();

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build();

        try {
            InputStream inputStream = s3Client.getObject(request);
            log.info("[BINARYCONTENT_S3_GET_SUCCESS] S3 파일 조회 성공: binaryContentId={}, bucket={}, key={}",
                    binaryContentId, props.getBucket(), key);
            return inputStream;
        } catch (S3Exception e) {
            log.warn("[BINARYCONTENT_S3_GET_FAIL] S3 파일 조회 실패: binaryContentId={}, bucket={}, key={}",
                    binaryContentId, props.getBucket(), key, e);
            throw new BinaryContentException(
                    ErrorCode.BINARY_CONTENT_CAN_NOT_READ,
                    Map.of("binaryContentId", binaryContentId)
            );
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto dto) {
        String key = dto.id().toString();

        // 어떤 파일을 가져올지 정의
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build();

        // presigned Url로 만들 때 옵션 설정
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(props.getPresignedUrlExpiration()))
                .getObjectRequest(getObjectRequest)
                .build();

        try {
            // 최종 결과
            PresignedGetObjectRequest presignedRequest =
                    s3Presigner.presignGetObject(presignRequest);

            log.info("[BINARYCONTENT_S3_DOWNLOAD_URL_SUCCESS] S3 presigned URL 생성 성공: binaryContentId={}, bucket={}, key={}",
                    dto.id(), props.getBucket(), key);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(presignedRequest.url().toString()))
                    .build();

        } catch (S3Exception e) {
            log.warn("[BINARYCONTENT_S3_DOWNLOAD_URL_FAIL] S3 presigned URL 생성 실패: binaryContentId={}, bucket={}, key={}",
                    dto.id(), props.getBucket(), key, e);
            throw new BinaryContentException(
                    ErrorCode.BINARY_CONTENT_CAN_NOT_READ,
                    Map.of("binaryContentId", dto.id())
            );
        }
    }

    @Recover
    public UUID recover(BinaryContentException e, UUID binaryContentId, byte[] bytes) {
        String requestId = MDC.get("requestId");

        notificationService.notifyAdmin(
                "S3 바이너리 저장 실패",
                """
                        작업: S3_BINARYCONTENT_SAVE
                        RequestId: %s
                        BinaryContentId: %s
                        Error: %s
                        """.formatted(requestId, binaryContentId, e.getMessage())
        );

        log.error(
                "[BINARYCONTENT_S3_SAVE_RECOVER] S3 파일 저장 최종 실패: binaryContentId={}, error={}",
                binaryContentId,
                e.getMessage(),
                e
        );

        throw e;
    }
}