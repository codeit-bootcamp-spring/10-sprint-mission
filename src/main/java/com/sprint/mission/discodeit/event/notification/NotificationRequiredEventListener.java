package com.sprint.mission.discodeit.event.notification;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.user.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// kafka로 알림처리를 위한 비활성화
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    List<ReadStatus> targets = readStatusRepository.findNotificationTargets(
        event.channelId(),
        event.senderId()
    );

    String title = "%s (#%s)".formatted(event.senderName(), event.channelName());

    for (ReadStatus target : targets) {
      notificationService.create(
          target.getUserId(),
          title,
          event.content()
      );
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) {
    notificationService.create(
        event.userId(),
        "권한이 변경되었습니다.",
        "%s -> %s".formatted(event.oldRole(), event.newRole())
    );
  }
}