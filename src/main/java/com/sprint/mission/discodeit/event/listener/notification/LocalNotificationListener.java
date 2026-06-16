package com.sprint.mission.discodeit.event.listener.notification;

import com.sprint.mission.discodeit.event.BinaryContentEvents;
import com.sprint.mission.discodeit.event.MessageEvents;
import com.sprint.mission.discodeit.event.UserEvents;
import com.sprint.mission.discodeit.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 로컬 개발 환경(dev)에서 Kafka 없이 직접 알림 이벤트를 처리하는 리스너입니다.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class LocalNotificationListener {

  private final NotificationEventService notificationEventService;

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageEvents.Created event) {
    notificationEventService.sendByMessageCreated(event.messageId());
  }

  @Async("ioTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserEvents.RoleUpdated event) {
    notificationEventService.sendByRoleUpdated(event.userId(), event.oldRole(), event.newRole());
  }

  @Async("ioTaskExecutor")
  @EventListener
  public void on(BinaryContentEvents.S3UploadFailed event) {
    notificationEventService.sendS3UploadFailedNotification(
        event.binaryContentId(), 
        event.mdcRequestId(), 
        event.errorMessage()
    );
  }
}
