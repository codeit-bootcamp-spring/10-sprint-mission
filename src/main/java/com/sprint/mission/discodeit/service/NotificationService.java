package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationDto create(UUID receiverId, String title, String content);
    List<NotificationDto> findAllByReceiverId(UUID receiverId);
    void delete(UUID notificationId, UUID requesterId);
}
