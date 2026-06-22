package com.sprint.mission.discodeit.notification.application.handler;

import com.sprint.mission.discodeit.notification.domain.event.NotificationCreatedEventWrapper;
import com.sprint.mission.discodeit.notification.domain.event.NotificationDeletedEvent;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationNotifierEventHandler {

  private final NotificationNotifier notifier;

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleCreated(NotificationCreatedEventWrapper wrapper) {
    wrapper.events().forEach(event -> notifier.notifyCreated(event.getReceiverId(), event));
  }

  @Async("stompWorker")
  @TransactionalEventListener
  public void handleDeleted(NotificationDeletedEvent event) {
    notifier.notifyDeleted(event.getReceiverId(), event);
  }
}
