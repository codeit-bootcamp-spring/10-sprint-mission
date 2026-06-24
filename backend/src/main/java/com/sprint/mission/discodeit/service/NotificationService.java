package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDto> findAllNotification(UUID receiverId);

  void deleteNotification(UUID notificationId, UUID authId);

  void createMessageNotifications(UUID channelId, UUID senderId, String content);

  void createRoleNotification(UUID userId, Role beforeRole, Role afterRole);

  void s3UploadFailedNotification(String requestId, UUID binaryContentId, String error);

}
