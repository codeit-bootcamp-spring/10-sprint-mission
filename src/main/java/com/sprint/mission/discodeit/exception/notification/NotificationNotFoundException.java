package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class NotificationNotFoundException extends NotificationException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.NOTIFICATION_NOT_FOUND;

  public NotificationNotFoundException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public NotificationNotFoundException() {
    super(DEFAULT_CODE);
  }

  public NotificationNotFoundException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public NotificationNotFoundException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
