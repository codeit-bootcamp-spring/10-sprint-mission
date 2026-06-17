package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<Notification> findAllByReceiverId(UUID receiverId);

  void deleteById(UUID notificationId, UUID requesterId);
}
