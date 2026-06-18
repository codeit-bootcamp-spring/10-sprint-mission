package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationsService {

  private final NotificationRepository notificationRepository;

  /// 사용자별 알림 목록 조회
  /// userId - List<NotificationDto> 형태로 캐시에 담긴다.
  @Cacheable(cacheManager = "redisCacheManager", cacheNames = "userNotifications", key = "#userId")
  @Transactional(readOnly = true)
  public List<NotificationDto> findAllByUserId(UUID userId) {
    log.debug("Find notifications: userId={}", userId);
    List<NotificationDto> notifications = notificationRepository.findAllDtoByReceiverId(userId);
    log.info("Found notifications: userId={}, count={}", userId, notifications.size());
    return notifications;
  }

  /// userId에 해당하는걸 모두 지워버림
  /// 즉 1 - 알림3을 지웠다고 하면 1-알림3만 캐시에서 삭제되는게 아니라
  /// 1이 가지는 알림을 전부 삭제.
  @CacheEvict(cacheManager = "redisCacheManager", value = "userNotifications", key = "#userId")
  @Transactional
  public void delete(UUID userId, UUID notificationId) {
    /// 알림 자체가 없다 - 404 NOT FOUND
    Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    /// 알림 존재하지만 다른 사용자 알림 - 403 FORBIDDEN
    if (!notification.getReceiver().getId().equals(userId)) {
      throw new AuthorizationDeniedException("해당 알림을 삭제할 권한이 없습니다.");
    }

    log.info("Read notification: userId={}, notificationId={}", userId, notificationId);
  }
}
