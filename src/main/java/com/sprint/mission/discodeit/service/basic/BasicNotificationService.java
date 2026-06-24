package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.event.sse.NotificationCreatedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final ApplicationEventPublisher applicationEventPublisher;

  @CacheEvict(value = "userNotifications", key = "#receiverId")
  @Transactional
  @Override
  public NotificationDto create(UUID receiverId, String title, String content) {
    log.debug("알림 생성 시작: receiverId={}, title={}", receiverId, title);
    Notification notification = notificationRepository.save(
        new Notification(receiverId, title, content));
    log.info("알림 생성 완료: id={}, receiverId={}", notification.getId(), receiverId);
    NotificationDto dto = notificationMapper.toDto(notification);
    applicationEventPublisher.publishEvent(new NotificationCreatedEvent(dto));
    return dto;
  }

  @Cacheable(value = "userNotifications", key = "#receiverId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회 시작: receiverId={}", receiverId);
    List<NotificationDto> dtos = notificationRepository
        .findAllByReceiverIdOrderByCreatedAtDesc(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
    log.info("알림 목록 조회 완료: receiverId={}, 조회된 항목 수={}", receiverId, dtos.size());
    return dtos;
  }

  @CacheEvict(value = "userNotifications", key = "#requesterId")
  @Transactional
  @Override
  public void delete(UUID notificationId, UUID requesterId) {
    log.debug("알림 삭제 시작: id={}", notificationId);
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.getReceiverId().equals(requesterId)) {
      throw NotificationForbiddenException.forNotification(notificationId);
    }

    notificationRepository.delete(notification);
    log.info("알림 삭제 완료: id={}", notificationId);
  }
}
