package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void notifyAdmin(String title, String content);

    List<NotificationDto> findAllByReceiverId(UUID receiverId);

    void delete(UUID notificationId, UUID receiverId);
}
