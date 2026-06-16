package com.sprint.mission.discodeit.eventlisteners;

import com.sprint.mission.discodeit.events.UserCreatedEvent;
import com.sprint.mission.discodeit.events.UserDeletedEvent;
import com.sprint.mission.discodeit.events.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserSseEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserCreatedEvent event) {
    sseService.broadcast("users.created", event.user());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserUpdatedEvent event) {
    sseService.broadcast("users.updated", event.user());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserDeletedEvent event) {
    sseService.broadcast("users.deleted", event.user());
  }
}
