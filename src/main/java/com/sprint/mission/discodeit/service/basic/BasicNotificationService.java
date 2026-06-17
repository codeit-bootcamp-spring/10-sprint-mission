package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.common.InvalidParameterException;
import com.sprint.mission.discodeit.exception.notification.NotificationForbiddenException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "userNotifications", key = "#receiverId")
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    requireNonNull(receiverId, "receiverId");

    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @CacheEvict(cacheNames = "userNotifications", key = "#receiverId")
  public void create(UUID receiverId, String title, String content) {
    requireNonNull(receiverId, "receiverId");
    requireNonNull(title, "title");
    requireNonNull(content, "content");

    User receiver = userRepository.findById(receiverId)
        .orElseThrow(() -> new UserNotFoundException(receiverId));

    Notification notification = new Notification(receiver, title, content);

    notificationRepository.save(notification);

  }

  @Override
  @Transactional
  @CacheEvict(cacheNames = "userNotifications", key = "#receiverId")
  public void delete(UUID notificationId, UUID receiverId) {
    requireNonNull(notificationId, "notificationId");
    requireNonNull(receiverId, "receiverId");

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId));

    if (!notification.getReceiverId().equals(receiverId)) {
      throw new NotificationForbiddenException();
    }

    notificationRepository.delete(notification);
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new InvalidParameterException(name);
    }
  }
}
