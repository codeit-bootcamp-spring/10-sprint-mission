package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.CacheConfig.CacheNames;
import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.payload.MessageCreatedPayload;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final CacheManager cacheManager;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void registerMessageCreatedNotification(MessageCreatedEvent event) {
    List<ReadStatus> readStatusesExceptAuthor = readStatusRepository
        .findAllByChannelId(event.messageCreatedPayload().channelId()).stream()
        .filter(ReadStatus::getNotificationEnabled)
        .filter(rs -> !rs.getUser().getId().equals(event.messageCreatedPayload().authorId()))
        .toList();

    if (readStatusesExceptAuthor.isEmpty()) {
      return;
    }

    List<Notification> notifications = readStatusesExceptAuthor.stream()
        .map(rs -> new Notification(
            rs.getUser().getId(),
            getMessageEventTitle(event.messageCreatedPayload()),
            event.messageCreatedPayload().content()))
        .toList();
    notificationRepository.saveAll(notifications);
    log.info("notification [MessageCrated] 생성: messageId={}, notificationSize={}",
        event.messageCreatedPayload().messageId(), notifications.size());

    // 캐시삭제
    Optional.ofNullable(cacheManager.getCache(CacheNames.NOTIFICATIONS_BY_USER))
        .ifPresent(cache -> notifications.forEach(n -> cache.evict(n.getReceiverId())));
  }

  @CacheEvict(cacheNames = CacheNames.NOTIFICATIONS_BY_USER, key = "#event.userId()")
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void registerRoleUpdatedNotification(RoleUpdatedEvent event) {
    Notification notification = new Notification(
        event.userId(),
        "권한이 변경되었습니다",
        event.oldRole() + " -> " + event.newRole());

    notificationRepository.save(notification);
    log.info("notification [RoleUpdated] 생성");
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void registerBinaryContentUploadFailNotification(BinaryContentUploadFailedEvent event) {
    List<Notification> notifications = userRepository.findAllByRole(Role.ADMIN).stream()
        .map(admin -> new Notification(admin.getId(), "S3 파일 업로드 실패", event.error()))
        .toList();
    notificationRepository.saveAll(notifications);
    log.info("notification [BinaryContentUploadFailedEvent] 생성: notificationSize={}",
        notifications.size());

    // 캐시삭제
    Optional.ofNullable(cacheManager.getCache(CacheNames.NOTIFICATIONS_BY_USER))
        .ifPresent(cache -> notifications.forEach(n -> cache.evict(n.getReceiverId())));
  }

  @Cacheable(CacheNames.NOTIFICATIONS_BY_USER)
  @Override
  public List<NotificationDto> getNotifications(UUID receiverId) {
    return notificationRepository.findAllByReceiverId(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @CacheEvict(cacheNames = CacheNames.NOTIFICATIONS_BY_USER, key = "#userId")
  @Transactional
  @Override
  public void deleteNotification(UUID notificationId, UUID userId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException());
    if (!notification.isOwnedBy(userId)) {
      throw new NotificationAccessDeniedException();
    }

    log.info("알람 삭제: id={}", notificationId);
    notificationRepository.delete(notification);
  }

  private String getMessageEventTitle(MessageCreatedPayload payload) {
    String channelName = payload.channelName() != null ?
        payload.channelName() : "개인채널";
    return payload.authorName() + " (#" + channelName + ")";
  }
}
