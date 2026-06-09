package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {

        UUID id = event.getBinaryContent().getId();
        log.debug("바이너리 데이터 저장 시작: id={}", event.getBinaryContent().getId());

        try {
            binaryContentStorage.put(id, event.getBytes());
            binaryContentService.updateStatus(id, BinaryContentStatus.SUCCESS);  // 성공
            log.debug("바이너리 데이터 저장 완료: id={}", id);
        } catch (Exception e) {
            log.error("바이너리 데이터 저장 실패: id={}", id, e);
            binaryContentService.updateStatus(id, BinaryContentStatus.FAIL);  // 실패
        }
    }
}
