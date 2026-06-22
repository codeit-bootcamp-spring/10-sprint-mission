package com.sprint.mission.discodeit.user.application.handler;

import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.user.domain.provider.UserNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserNotifierEventHandler {

  private final UserNotifier notifier;

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleCreated(UserCreatedEvent event) {
    notifier.notifyCreated(event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleUpdated(UserUpdatedEvent event) {
    notifier.notifyUpdated(event);
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleDeleted(UserDeletedEvent event) {
    notifier.notifyDeleted(event);
  }
}
