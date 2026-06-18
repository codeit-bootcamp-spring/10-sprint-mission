package com.sprint.mission.discodeit.event.user;

import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(UserCreatedEvent event) {
    sseService.broadcast(
        "users.created",
        event.user()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(UserUpdatedEvent event) {
    sseService.broadcast(
        "users.updated",
        event.user()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(UserDeletedEvent event) {
    sseService.broadcast(
        "users.deleted",
        event.userId()
    );
  }


}
