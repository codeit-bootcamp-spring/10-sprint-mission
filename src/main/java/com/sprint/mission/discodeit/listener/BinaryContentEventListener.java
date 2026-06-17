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

/*
    BinaryContentEventListener
    --------------------------
    메인 트랜잭션이 성공적으로 커밋되었을 때만 BinaryContentStorage를 호출하는 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;

    private final BinaryContentService binaryContentService;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent binaryContentCreatedEvent) {
        try {
            // 실제 스토리지에 데이터 저장
            binaryContentStorage.put(binaryContentCreatedEvent.binaryContentId(), binaryContentCreatedEvent.rawData());
            binaryContentService.updateStatus(binaryContentCreatedEvent.binaryContentId(), BinaryContentStatus.SUCCESS);

            log.info("[EVENT] 첨부파일 데이터 저장 완료: id={}", binaryContentCreatedEvent.binaryContentId());
        }  catch (Exception e) {
            binaryContentService.updateStatus(binaryContentCreatedEvent.binaryContentId(), BinaryContentStatus.FAIL);

            log.error("[EVENT] 첨부파일 데이터 저장 실패: id={}", binaryContentCreatedEvent.binaryContentId(), e);
        }
    }
}