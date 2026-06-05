package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final NotificationMapper notificationMapper;

  @Cacheable(cacheNames = "notificationsByUser", key = "#receiverId")
  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId).stream()
        .map(notificationMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public NotificationDto create(UUID receiverId, String title, String content) {
    User receiver =
        userRepository
            .findById(receiverId)
            .orElseThrow(() -> UserNotFoundException.withId(receiverId));

    Notification notification = new Notification(receiver, title, content);
    notificationRepository.save(notification);

    return notificationMapper.toDto(notification);
  }

  @Transactional
  @Override
  public void delete(UUID notificationId, UUID requesterId) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    if (!notification.isOwnedBy(requesterId)) {
      throw NotificationAccessDeniedException.withId(notificationId, requesterId);
    }

    notificationRepository.delete(notification);
  }
}
