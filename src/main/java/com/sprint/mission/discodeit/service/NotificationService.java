package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notificationdto.NotificationDto;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<NotificationDto> findAllByCurrentUser();

  void delete(UUID notificationId);
}
