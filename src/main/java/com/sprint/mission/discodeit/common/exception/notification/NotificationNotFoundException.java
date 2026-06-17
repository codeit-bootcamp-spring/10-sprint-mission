package com.sprint.mission.discodeit.common.exception.notification;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException(Map<String, Object> details) {
    super(ErrorCode.NOTIFICATION_NOT_FOUND, details);
  }
}