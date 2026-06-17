package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    NotificationNotFoundException
    ------------------------------
    해당 알림이 시스템 내에 존재하지 않을 때 발생하는 예외 클래스
 */
public class NotificationNotFoundException extends NotificationException{
    public NotificationNotFoundException(UUID notificationId) {
        super(
                ErrorCode.NOTIFICATION_NOT_FOUND,
                Map.of("notificationId", notificationId)
        );
    }
}
