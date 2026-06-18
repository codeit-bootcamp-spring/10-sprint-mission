package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.UserCreatedEvent;
import com.sprint.mission.discodeit.event.UserDeletedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
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
public class UserSseEventListener {

  private final SseService sseService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserCreated(UserCreatedEvent event) {
    sseService.broadcast(
        "users.created",
        event.user()
    );

    log.info("[SUCCESS] SSE User Created Sent: id={}", event.user().getId());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserUpdated(UserUpdatedEvent event) {
    sseService.broadcast(
        "users.updated",
        event.user()
    );

    log.info("[SUCCESS] SSE User Updated Sent: id={}", event.user().getId());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserDeleted(UserDeletedEvent event) {
    sseService.broadcast(
        "users.deleted",
        event.user()
    );

    log.info("[SUCCESS] SSE User Deleted Sent: id={}", event.user().getId());
  }
}
