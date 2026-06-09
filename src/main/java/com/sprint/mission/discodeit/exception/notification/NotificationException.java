package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class NotificationException extends DiscodeitException {

  protected NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
