package com.sprint.mission.discodeit.notification.domain.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.UUID;

public class NotificationException extends DiscodeitException {

  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotificationException(ErrorCode errorCode, UUID id) {
    super(errorCode, id);
  }
}
