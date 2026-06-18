package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException(UUID id) {
    super(ErrorCode.NOTIFICATION_NOT_FOUND, Map.of("id", id));
  }
}
