package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.CacheNames;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationForbiddenException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Cacheable(cacheNames = CacheNames.NOTIFICATIONS, key = "#receiverId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회 시작: receiverId={}", receiverId);
    List<NotificationDto> dtos = notificationRepository
        .findAllByReceiverIdOrderByCreatedAtDesc(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
    log.info("알림 목록 조회 완료: receiverId={}, count={}", receiverId, dtos.size());
    return dtos;
  }

  @CacheEvict(cacheNames = CacheNames.NOTIFICATIONS, key = "#requesterId")
  @Transactional
  @Override
  public void delete(UUID notificationId, UUID requesterId) {
    log.debug("알림 삭제 시작: id={}, requesterId={}", notificationId, requesterId);
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.getReceiverId().equals(requesterId)) {
      throw NotificationForbiddenException.forUser(requesterId, notificationId);
    }

    notificationRepository.delete(notification);
    log.info("알림 삭제 완료: id={}", notificationId);
  }
}
