package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class NotificationAccessDeniedException extends DiscodeitException {

  public NotificationAccessDeniedException(UUID notificationId, UUID userId) {
    super(ErrorCode.NOTIFICATION_ACCESS_DENIED);

    addDetail("notificationId", notificationId);
    addDetail("userId", userId);
  }

  public static NotificationAccessDeniedException withId(UUID notificationId, UUID userId) {
    return new NotificationAccessDeniedException(notificationId, userId);
  }
}
