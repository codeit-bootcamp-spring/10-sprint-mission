package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Cacheable(cacheNames = "notifications", key = "#receiverId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회 시작 - receiverId: {}", receiverId);
    List<Notification> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId);
    log.info("알림 목록 조회 완료 - receiverId: {}, count: {}", receiverId, notifications.size());
    return notifications.stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @CacheEvict(cacheNames = "notifications", key = "#requesterId")
  @Transactional
  @Override
  public void delete(UUID notificationId, UUID requesterId) {
    log.debug("알림 삭제 시작 - notificationId: {}, requesterId: {}", notificationId, requesterId);
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.getReceiver().getId().equals(requesterId)) {
      throw new AccessDeniedException("본인의 알림만 확인할 수 있습니다.");
    }

    notificationRepository.delete(notification);
    log.info("알림 삭제 완료 - notificationId: {}", notificationId);
  }
}
