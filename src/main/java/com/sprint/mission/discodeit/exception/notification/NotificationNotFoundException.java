package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class NotificationNotFoundException extends DiscodeitException {

  public NotificationNotFoundException(UUID notificationId) {
    super(ErrorCode.NOTIFICATION_NOT_FOUND);
    addDetail("notificationId", notificationId);
  }

  public static NotificationNotFoundException withId(UUID notificationId) {
    return new NotificationNotFoundException(notificationId);
  }
}
