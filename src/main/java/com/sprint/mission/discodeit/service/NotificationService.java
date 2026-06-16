package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 알림의 핵심 CRUD 기능을 제공하는 서비스 인터페이스입니다.
 */
public interface NotificationService {
  List<NotificationDto> findAllByUserId(UUID userId);
  void create(UUID userId, String title, String content);
  void delete(UUID userId, UUID notificationId);
}
