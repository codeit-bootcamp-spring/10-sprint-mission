package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentCreatedEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async("asyncTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BinaryContentCreatedEvent event) {
        try {
            binaryContentStorage.put(event.binaryContentId(), event.bytes());
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
        } catch (Exception e) {
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
            log.error("바이너리 데이터 저장 실패: id={}", event.binaryContentId(), e);
        }
    }
}
