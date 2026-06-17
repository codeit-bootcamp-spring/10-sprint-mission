package com.sprint.mission.discodeit.exception.notification;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class NotificationForbiddenException extends NotificationException {
    public NotificationForbiddenException(UUID notificationId, UUID requesterId) {
        super(ErrorCode.ACCESS_DENIED, Map.of("notificationId", notificationId, "requesterId", requesterId));
    }
}
