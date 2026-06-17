package com.sprint.mission.discodeit.common.exception.notification;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;

public class NotificationException extends DiscodeitException {

  public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}