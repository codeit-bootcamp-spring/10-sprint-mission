package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  void registerMessageCreatedNotification(MessageCreatedEvent event);

  void registerRoleUpdatedNotification(RoleUpdatedEvent event);

  void registerBinaryContentUploadFailNotification(BinaryContentUploadFailedEvent event);

  List<NotificationDto> getNotifications(UUID receiverId);

  void deleteNotification(UUID notificationId, UUID userId);

}
