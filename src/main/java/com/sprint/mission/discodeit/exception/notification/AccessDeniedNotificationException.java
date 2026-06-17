package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;

/*
    AccessDeniedNotificationException
    ---------------------------------
    해당 알림의 소유자가 아닌 사용자가 알림에 접근하고자 할 때 발생하는 예외 클래스
 */
public class AccessDeniedNotificationException extends NotificationException {

    public AccessDeniedNotificationException() {
        super(ErrorCode.ACCESS_DENIED_NOTIFICATION);
    }
}
