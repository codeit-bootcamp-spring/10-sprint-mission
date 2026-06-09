package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  NotificationDto create(NotificationCreateRequest notificationCreateRequest);

  List<NotificationDto> getNotifications(UUID userId);

  void delete(UUID userId, UUID notificationId);

}
