package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class NotificationAccessDeniedException extends NotificationException {

  public NotificationAccessDeniedException(Map<String, Object> details) {
    super(ErrorCode.NOTIFICATION_ACCESS_DENIED, details);
  }
}
