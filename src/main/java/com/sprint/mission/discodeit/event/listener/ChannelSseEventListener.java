package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {

  private final SseService sseService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleChannelCreated(ChannelCreatedEvent event) {
    sseService.broadcast(
        "channels.created",
        event.channel()
    );

    log.info("[SUCCESS] SSE Channel Created Sent: id={}", event.channel().getId());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleChannelUpdated(ChannelUpdatedEvent event) {
    sseService.broadcast(
        "channels.updated",
        event.channel()
    );

    log.info("[SUCCESS] SSE Channel Updated Sent: id={}", event.channel().getId());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleChannelDeleted(ChannelDeletedEvent event) {
    sseService.broadcast(
        "channels.deleted",
        event.channel()
    );

    log.info("[SUCCESS] SSE Channel Deleted Sent: id={}", event.channel().getId());
  }
}
