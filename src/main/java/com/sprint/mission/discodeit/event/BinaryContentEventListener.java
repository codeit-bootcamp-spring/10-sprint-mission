package com.sprint.mission.discodeit.event;

import static com.sprint.mission.discodeit.entity.BinaryContentStatus.FAIL;
import static com.sprint.mission.discodeit.entity.BinaryContentStatus.SUCCESS;

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
public class BinaryContentEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentCreatedEvent event) {
    try {
      binaryContentStorage.put(event.binaryContentId(), event.bytes());
      binaryContentService.updateStatus(event.binaryContentId(), SUCCESS);
      log.info("Binary content upload completed: id={}", event.binaryContentId());
    } catch (Exception e) {
      binaryContentService.updateStatus(event.binaryContentId(), FAIL);
      log.error("Binary content upload failed: id={}", event.binaryContentId(), e);
    }
  }
}
