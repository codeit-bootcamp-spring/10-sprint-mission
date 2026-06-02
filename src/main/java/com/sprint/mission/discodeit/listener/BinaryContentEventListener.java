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

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async("asyncExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
    log.debug("[BINARY_CONTENT] BinaryContent 생성 이벤트 수신 id={}", event.binaryContentId());
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());

      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
      log.info("[BINARY_CONTENT] 바이너리 데이터 업로드 성공: id={}", event.binaryContentId());

    } catch (Exception e) {
      log.error("[BINARY_CONTENT] 바이너리 데이터 업로드 실패: id={}", event.binaryContentId());

      binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
    }
  }
}
