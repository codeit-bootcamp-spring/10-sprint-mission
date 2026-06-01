package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
    log.debug("[BINARY_CONTENT] BinaryContent 생성 이벤트 수신 id={}", event.id());
    try {
      binaryContentStorage.put(event.id(), event.bytes());

      binaryContentService.updateStatus(event.id(), BinaryContentStatus.SUCCESS);
      log.info("[BINARY_CONTENT] 바이너리 데이터 업로드 성공: id={}", event.id());

    } catch (Exception e) {
      log.error("[BINARY_CONTENT] 바이너리 데이터 업로드 실패: id={}", event.id());

      binaryContentService.updateStatus(event.id(), BinaryContentStatus.FAIL);
    }
  }
}
