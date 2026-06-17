package com.sprint.mission.discodeit.notification.event;

import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.repository.JPAReadStatusRepository;
import com.sprint.mission.discodeit.notification.entity.Notification;
import com.sprint.mission.discodeit.notification.repository.JPANotificationRepository;
import com.sprint.mission.discodeit.user.Role;
import com.sprint.mission.discodeit.user.event.RoleUpdatedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final JPAReadStatusRepository jpaReadStatusRepository;
  private final JPANotificationRepository jpaNotificationRepository;

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    //알림여부 활성화 조회
    List<ReadStatus> users = jpaReadStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
        event.message().getChannel().getId()
    );

    // 유저들의 읽음상태 체크
    for (ReadStatus readStatus : users) {
      if (readStatus.getUser().getId().equals(event.message().getAuthor().getId())) {
        // 메시지 보낸사람은 알림 제외
        continue;
      }
      Notification notification = new Notification(
          readStatus.getUser().getId(),
          event.message().getAuthor().getUsername()
              + " (#" + event.message().getChannel().getName() + ")",
          event.message().getContent()
      );
      jpaNotificationRepository.save(notification);
    }

  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    Role oldRole = event.oldRole();
    Role newRole = event.newRole();
    Notification notification = new Notification(
        event.userId(),
        "권한이 변경되었습니다.",
        oldRole.toString() + " -> " + newRole.toString()
    );
    jpaNotificationRepository.save(notification);
  }

}
