package com.sprint.mission.discodeit.notification.service;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import com.sprint.mission.discodeit.common.exception.notification.NotificationException;
import com.sprint.mission.discodeit.common.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.notification.dto.NotificationDto;
import com.sprint.mission.discodeit.notification.entity.Notification;
import com.sprint.mission.discodeit.notification.repository.JPANotificationRepository;
import com.sprint.mission.discodeit.sse.service.SseService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final JPANotificationRepository jpaNotificationRepository;
  private final SseService sseService;

  @Override
  @Cacheable(value = "notificationsByUser", key = "#receiverId")
  @Transactional(readOnly = true)
  public List<NotificationDto> findNotificationsByReceiverId(UUID receiverId) {
    return jpaNotificationRepository.findAllByReceiverId(receiverId)
        .stream()
        .map(notification -> new NotificationDto(
            notification.getId(),
            notification.getCreatedAt(),
            notification.getReceiverId(),
            notification.getTitle(),
            notification.getContent()
        ))
        .toList();
  }

  @Override
  @Transactional
  @CacheEvict(value = "notificationsByUser", key = "#userId")
  public void confirmNotification(UUID notificationId, UUID userId) {
    Notification notification = jpaNotificationRepository.findById(notificationId)
        .orElseThrow(
            () -> new NotificationNotFoundException(Map.of("notificationId", notificationId)));

    if (!notification.getReceiverId().equals(userId)) {
      throw new NotificationException(
          ErrorCode.NOTIFICATION_FORBIDDEN, Map.of("notificationId", notificationId));
    }

    jpaNotificationRepository.delete(notification);
  }

  @Override
  @Transactional
  @CacheEvict(value = "notificationsByUser", key = "#receiverId")
  public Notification create(UUID receiverId, String title, String content) {
    Notification notification = new Notification(
        receiverId,
        title,
        content
    );
    jpaNotificationRepository.save(notification);
    NotificationDto notificationDto = new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getReceiverId(),
        notification.getTitle(),
        notification.getContent()
    );
    sseService.send(List.of(receiverId), "notifications.created", notificationDto);
    return notification;
  }
}
