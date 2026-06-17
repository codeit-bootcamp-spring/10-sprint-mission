package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class NotificationForbiddenException extends NotificationException {

  public NotificationForbiddenException() {
    super(ErrorCode.FORBIDDEN);
  }

  public static NotificationForbiddenException forNotification(UUID id) {
    NotificationForbiddenException exception = new NotificationForbiddenException();
    exception.addDetail("id", id);
    return exception;
  }

}
