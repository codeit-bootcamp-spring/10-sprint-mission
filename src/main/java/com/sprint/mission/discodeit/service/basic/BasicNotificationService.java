package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final NotificationMapper notificationMapper;

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @CacheEvict(value = "notificationListCache", key = "#request.receiverId()")
  public NotificationDto create(NotificationCreateRequest request) {
    User receiver = userRepository.getReferenceById(request.receiverId());
    Notification notification = Notification.create(request.title(), request.content(), receiver);
    notificationRepository.save(notification);
    return notificationMapper.toDto(notification);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "notificationListCache", key = "#userId")
  public List<NotificationDto> getNotifications(UUID userId) {
    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Override
  @PreAuthorize("hasPermission(#notificationId, 'NOTIFICATION', 'DELETE')")
  @CacheEvict(value = "notificationListCache", key = "#userId")
  public void delete(UUID userId, UUID notificationId) {
    notificationRepository.deleteById(notificationId);
  }
}
