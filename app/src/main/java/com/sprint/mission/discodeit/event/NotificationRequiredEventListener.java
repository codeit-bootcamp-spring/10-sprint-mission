package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "discodeit.kafka.enabled", havingValue = false)
@Component
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;

  @Async("asyncExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    notificationService.registerMessageCreatedNotification(event);
  }

  @Async("asyncExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    notificationService.registerRoleUpdatedNotification(event);
  }

  @Async("asyncExecutor")
  @EventListener
  public void on(BinaryContentUploadFailedEvent event) {
    notificationService.registerBinaryContentUploadFailNotification(event);
  }
}
