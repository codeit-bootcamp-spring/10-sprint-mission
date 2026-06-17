package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    // 알림 조회
    List<NotificationDto> findAll(UUID userId);

    // 알림 삭제
    void delete(UUID notificationId, UUID userId);
}
