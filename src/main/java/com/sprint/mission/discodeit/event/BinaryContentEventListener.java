package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentStatusUpdater binaryContentStatusUpdater;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
    log.debug("바이너리 데이터 저장 시작: id={}, size={}",
        event.binaryContentId(), event.bytes().length);
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContentStatusUpdater.updateStatus(event.binaryContentId(),
          BinaryContentStatus.SUCCESS);
      log.info("바이너리 데이터 저장 완료: id={}", event.binaryContentId());
    } catch (Exception e) {
      log.error("바이너리 데이터 저장 실패: id={}", event.binaryContentId(), e);
      binaryContentStatusUpdater.updateStatus(event.binaryContentId(),
          BinaryContentStatus.FAIL);
    }
  }
}
