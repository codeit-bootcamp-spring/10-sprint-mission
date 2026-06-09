package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.CacheNames;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private static final int NOTIFICATION_TITLE_MAX_LENGTH = 100;
  private static final int NOTIFICATION_CONTENT_MAX_LENGTH = 500;

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final NotificationMapper notificationMapper;
  private final CacheManager cacheManager;

  @CacheEvict(cacheNames = CacheNames.NOTIFICATIONS, key = "#receiverId")
  @Transactional
  @Override
  public NotificationDto create(UUID receiverId, String title, String content) {
    User receiver = userRepository.findById(receiverId)
        .orElseThrow(() -> UserNotFoundException.withId(receiverId));
    Notification notification = new Notification(
        receiver,
        truncate(title, NOTIFICATION_TITLE_MAX_LENGTH),
        truncate(content, NOTIFICATION_CONTENT_MAX_LENGTH)
    );
    notificationRepository.save(notification);
    log.info("알림 생성 완료: id={}, receiverId={}", notification.getId(), receiverId);
    return notificationMapper.toDto(notification);
  }

  @Transactional(readOnly = true)
  @Override
  public NotificationDto find(UUID notificationId) {
    return notificationRepository.findById(notificationId)
        .map(notificationMapper::toDto)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
  }

  @Cacheable(cacheNames = CacheNames.NOTIFICATIONS, key = "#receiverId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @PreAuthorize("principal.userDto.id == @basicNotificationService.find(#notificationId).receiverId")
  @Transactional
  @Override
  public void delete(UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
    UUID receiverId = notification.getReceiver().getId();

    notificationRepository.delete(notification);
    evictNotificationsByReceiver(receiverId);
    log.info("알림 확인 완료: id={}", notificationId);
  }

  private void evictNotificationsByReceiver(UUID receiverId) {
    Cache cache = cacheManager.getCache(CacheNames.NOTIFICATIONS);
    if (cache != null) {
      cache.evict(receiverId);
    }
  }

  private String truncate(String value, int maxLength) {
    if (value.length() <= maxLength) {
      return value;
    }
    return value.substring(0, maxLength - 3) + "...";
  }
}
