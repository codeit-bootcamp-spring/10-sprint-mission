package com.sprint.mission.discodeit.notification.service;

import com.sprint.mission.discodeit.notification.dto.NotificationDto;
import com.sprint.mission.discodeit.notification.entity.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDto> findNotificationsByReceiverId(UUID receiverId);

  void confirmNotification(UUID notificationId, UUID userId);

  Notification create(UUID receiverId, String title, String content);
}
