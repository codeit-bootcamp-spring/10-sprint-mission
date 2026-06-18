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

@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentEventListener {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Async("asyncExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentPut(BinaryContentCreatedEvent event) {
    BinaryContentStatus status = null;
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      status = BinaryContentStatus.SUCCESS;
      log.info("BinaryContent 저장 성공: contentId={}", event.binaryContentId());
    } catch (Exception e) {
      status = BinaryContentStatus.FAIL;
      log.error("BinaryContent 저장 실패: contentId={}", event.binaryContentId());
    } finally {
      binaryContentService.updateStatus(event.binaryContentId(), status);
    }
  }
}
