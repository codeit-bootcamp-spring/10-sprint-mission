package com.sprint.mission.discodeit.event.channel;

import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChannelEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ChannelCreatedEvent event) {
    sseService.broadcast(
        "channels.created",
        event.channel()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ChannelUpdatedEvent event) {
    sseService.broadcast(
        "channels.updated",
        event.channel()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(ChannelDeletedEvent event) {
    sseService.broadcast(
        "channels.deleted",
        event.channelId()
    );
  }
}
