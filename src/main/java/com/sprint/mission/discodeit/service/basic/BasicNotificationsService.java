package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationsService {

  private final NotificationRepository notificationRepository;

  @Transactional(readOnly = true)
  public List<NotificationDto> findAllByUserId(UUID userId) {
    log.debug("Find notifications: userId={}", userId);
    List<NotificationDto> notifications = notificationRepository.findAllDtoByReceiverId(userId);
    log.info("Found notifications: userId={}, count={}", userId, notifications.size());
    return notifications;
  }

  @Transactional
  public void delete(UUID userId, UUID notificationId) {
    if (!notificationRepository.existsByIdAndReceiverId(notificationId, userId)) {
      throw NotificationNotFoundException.withId(notificationId);
    }

    notificationRepository.deleteById(notificationId);
    log.info("Read notification: userId={}, notificationId={}", userId, notificationId);
  }
}
