package com.sprint.mission.discodeit.eventlisteners;

import com.sprint.mission.discodeit.events.ChannelCreatedEvent;
import com.sprint.mission.discodeit.events.ChannelDeletedEvent;
import com.sprint.mission.discodeit.events.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelCreatedEvent event) {
    sseService.broadcast("channels.created", event.channel());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelUpdatedEvent event) {
    sseService.broadcast("channels.updated", event.channel());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelDeletedEvent event) {
    sseService.broadcast("channels.deleted", event.channel());
  }
}
