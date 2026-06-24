package com.sprint.mission.discodeit.dto.sse;

import com.sprint.mission.discodeit.event.binarycontent.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseBinaryContentEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(BinaryContentUpdatedEvent event) {
    sseService.broadcast(
        "binaryContents.updated",
        event.binaryContent()
    );
  }

}
