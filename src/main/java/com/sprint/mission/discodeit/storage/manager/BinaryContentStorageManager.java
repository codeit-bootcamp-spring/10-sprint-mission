package com.sprint.mission.discodeit.storage.manager;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentStorageManager {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;
    private final ApplicationEventPublisher eventPublisher;

    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void store(UUID binaryContentId, byte[] bytes) {
        binaryContentStorage.put(binaryContentId, bytes);
        binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
        log.info("바이너리 저장 완료: contentId={}", binaryContentId);
    }

    @Recover
    public void recover(RuntimeException e, UUID binaryContentId, byte[] bytes) {
        binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);

        String requestId = MDC.get("requestId");
        String errorMessage = e.getMessage();

        log.error("[바이너리 저장 최종 실패] task=binaryContentStore, requestId={}, contentId={}, message={}",
                requestId, binaryContentId, errorMessage);

        eventPublisher.publishEvent(
                new S3UploadFailedEvent(binaryContentId, "binaryContentStore", requestId, errorMessage));
    }
}
