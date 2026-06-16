package com.sprint.mission.discodeit.eventlisteners;

import com.sprint.mission.discodeit.events.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentUpdatedEventListener {

  private static final String EVENT_NAME = "binaryContents.updated";

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentUpdatedEvent event) {
    sseService.broadcast(EVENT_NAME, event.binaryContent());
  }
}
