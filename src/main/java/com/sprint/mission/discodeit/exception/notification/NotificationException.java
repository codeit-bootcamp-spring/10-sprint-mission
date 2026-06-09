package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class NotificationException extends DiscodeitException {
    public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
