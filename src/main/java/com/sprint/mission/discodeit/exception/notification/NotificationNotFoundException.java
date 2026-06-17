package com.sprint.mission.discodeit.exception.notification;

import static com.sprint.mission.discodeit.exception.ErrorCode.NOTIFICATION_NOT_FOUND;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import java.util.UUID;

public class NotificationNotFoundException extends DiscodeitException {

  public NotificationNotFoundException() {
    super(NOTIFICATION_NOT_FOUND);
  }
}
