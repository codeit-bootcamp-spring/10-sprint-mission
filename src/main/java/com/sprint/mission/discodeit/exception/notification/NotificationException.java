package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/*
    NotificationException
    -----------------
    Notification 도메인 내 최상위 예외 클래스
 */
public class NotificationException extends DiscodeitException {
    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotificationException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
