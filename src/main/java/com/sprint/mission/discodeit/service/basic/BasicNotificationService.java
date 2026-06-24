package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.event.NotificationEvents;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 알림 관련 핵심 비즈니스 로직을 처리하는 기본 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Cacheable(value = "userNotificationsCache", key = "#userId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByUserId(UUID userId) {
    List<NotificationDto> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId).stream()
        .map(notificationMapper::toDto)
        .toList();
    
    log.debug("[Notification] 사용자 알림 조회: UserId={}, Count={}", userId, notifications.size());
    return notifications;
  }

  @Transactional(readOnly = true)
  @Override
  public NotificationDto find(UUID notificationId) {
    return notificationRepository.findById(notificationId)
        .map(notificationMapper::toDto)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
  }

  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Transactional
  @Override
  public void create(UUID userId, String title, String content) {
    User receiver = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Notification notification = new Notification(receiver, title, content);
    Notification savedNotification = notificationRepository.save(notification);
    
    log.info("[Notification] 알림 생성 완료: ReceiverId={}, Title={}", userId, title);

    eventPublisher.publishEvent(new NotificationEvents.Created(savedNotification.getId(), userId));
  }

  @CacheEvict(value = "userNotificationsCache", key = "#userId")
  @Transactional
  @Override
  public void delete(UUID userId, UUID notificationId) {
    Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, userId)
        .orElseThrow(() -> NotificationAccessDeniedException.withId(notificationId, userId));

    notificationRepository.delete(notification);
    log.info("[Notification] 알림 삭제 완료: ID={}, ReceiverId={}", notificationId, userId);
  }
}
