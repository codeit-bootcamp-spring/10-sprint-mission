package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Transactional(readOnly = true)
  public List<NotificationDto> findAll() {
    List<Notification> notifications = notificationRepository.findAll();
    log.debug("[NOTIFICATION] 알림 목록 조회 완료: notificationCount={}", notifications.size());
    return notifications.stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Override
  public void delete(UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(
            () -> new NotificationNotFoundException(Map.of("notificationId", notificationId)));
    notificationRepository.delete(notification);
    log.debug("[NOTIFICATION] 알림 확인 완료: notificationId={}", notificationId);
  }
}
