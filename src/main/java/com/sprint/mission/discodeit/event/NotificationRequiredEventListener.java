package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;

  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    readStatusRepository.findAllByChannelIdAndNotificationEnabledTrueWithUser(event.channelId())
        .stream()
        .map(ReadStatus::getUser)
        .filter(user -> !user.getId().equals(event.authorId()))
        .forEach(user -> notificationService.create(
            user.getId(),
            messageNotificationTitle(event),
            event.content()
        ));
  }

  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    notificationService.create(
        event.userId(),
        "권한이 변경되었습니다.",
        event.previousRole().name() + " -> " + event.newRole().name()
    );
  }

  private String messageNotificationTitle(MessageCreatedEvent event) {
    return event.authorUsername() + " (#" + channelLabel(event.channelName(), event.channelId())
        + ")";
  }

  private String channelLabel(String channelName, UUID channelId) {
    if (channelName != null && !channelName.isBlank()) {
      return channelName;
    }
    return channelId.toString();
  }
}
