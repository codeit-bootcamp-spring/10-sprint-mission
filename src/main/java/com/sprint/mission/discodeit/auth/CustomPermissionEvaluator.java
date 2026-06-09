package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import java.io.Serializable;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

  private final MessageRepository messageRepository;
  private final NotificationRepository notificationRepository;

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    return false;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    UUID loginUserId = userDetails.getUserDto().id();
    UUID targetUuid = (UUID) targetId;

    if (targetType.equalsIgnoreCase("message")) {
      Message message = messageRepository.findById(targetUuid)
          .orElseThrow(() -> new MessageNotFoundException().addDetail("messageId", targetUuid));
      return message.getAuthor().getId().equals(loginUserId);
    } else if (targetType.equalsIgnoreCase("notification")) {
      Notification notification = notificationRepository.findById(targetUuid)
          .orElseThrow(
              () -> new NotificationNotFoundException().addDetail("notificationId", targetUuid));
      return notification.getReceiver().getId().equals(loginUserId);
    }
    return false;
  }
}
