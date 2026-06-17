package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(String key, Object value) {
        super(ErrorCode.NOTIFICATION_NOT_FOUNT, key, value);
    }
}
