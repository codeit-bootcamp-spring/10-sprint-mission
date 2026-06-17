package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NotificationService {

    void create(Set<UUID> receiverIds, String title, String content);

    List<NotificationDto> findAllByReceiverId(UUID receiverId);

    void deleteByReceiverId(UUID receiverId, UUID notificationId);
}
