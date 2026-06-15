package com.sprint.mission.discodeit.notification.application.handler;

import com.sprint.mission.discodeit.binarycontent.domain.event.UploadFailedNotificationEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.notification.application.service.NotificationService;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationChannelResolver;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationReadStatusResolver;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationUserResolver;
import com.sprint.mission.discodeit.user.domain.event.UserRoleUpdateEvent;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final String ROLE_UPDATE_TITLE = "role updated";
  private final String ROLE_UPDATE_MESSAGE_TEMPLATE = "%s -> %s";
  private final String MESSAGE_CREATED_TITLE_TEMPLATE = "%s (#%s)";
  private final NotificationService service;
  private final NotificationUserResolver userResolver;
  private final NotificationChannelResolver channelResolver;
  private final NotificationReadStatusResolver readStatusResolver;


  @Async("notificationWorker")
  @TransactionalEventListener
  public void on(UserRoleUpdateEvent event) {
    String message = ROLE_UPDATE_MESSAGE_TEMPLATE.formatted(event.oldRole(), event.newRole());
    service.create(List.of(event.id()), ROLE_UPDATE_TITLE, message);
  }

  @Async("notificationWorker")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    String username = userResolver.getUsername(event.senderId());
    String channelName = channelResolver.getChannelName(event.channelId());
    List<UUID> receiverIds
        = readStatusResolver.findUserIdsByChannelIdAndNotificationEnabledIsTrue(event.channelId());
    String title = MESSAGE_CREATED_TITLE_TEMPLATE.formatted(username, channelName);
    service.create(receiverIds, title, event.content());
  }

  @Async("notificationWorker")
  @TransactionalEventListener
  public void on(UploadFailedNotificationEvent event) {
    service.sendToAdmin(event.title(), event.messages());
  }
}
