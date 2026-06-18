package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.sse.SseService;
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

  private final BinaryContentStorage storage;
  private final BinaryContentService binaryContentService;
  private final SseService sseService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
    try {
      storage.put(event.id(), event.data());
      binaryContentService.updateStatus(
          event.id(),
          BinaryContentStatus.SUCCESS
      );

      log.info("[SUCCESS] Binary Content Uploaded: id={}", event.id());

    } catch (Exception e) {
      binaryContentService.updateStatus(event.id(), BinaryContentStatus.FAIL);

      log.error("[FAIL] Binary Content Upload Failed: id={}", event.id(), e);
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBinaryContentUpdatedEvent(BinaryContentUpdatedEvent event) {
    sseService.broadcast(
        "binaryContents.updated",
        event.binaryContent()
    );

    log.info(
        "[SUCCESS] SSE Binary Content Updated Event Sent: id={}",
        event.binaryContent().getId()
    );
  }
}
