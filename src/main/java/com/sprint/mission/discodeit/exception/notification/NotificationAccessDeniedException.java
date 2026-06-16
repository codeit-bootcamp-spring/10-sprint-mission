package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class NotificationAccessDeniedException extends NotificationException {
  private NotificationAccessDeniedException(Map<String, Object> details) {
    super(ErrorCode.NOTIFICATION_ACCESS_DENIED, details);
  }

  public static NotificationAccessDeniedException withId(UUID notificationId, UUID userId) {
    return new NotificationAccessDeniedException(
        Map.of(
        "notificationId", notificationId,
        "userId",userId
        )
    );
  }
}