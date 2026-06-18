package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class NotificationAccessDeniedException extends NotificationException {

  public NotificationAccessDeniedException() {
    super(ErrorCode.NOTIFICATION_ACCESS_DENIED);
  }
}
