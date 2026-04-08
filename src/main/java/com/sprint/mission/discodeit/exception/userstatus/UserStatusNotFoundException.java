package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {

    private UserStatusNotFoundException(Map<String, Object> details) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, details);
    }

    public static UserStatusNotFoundException byUserStatusId(UUID userStatusId) {
        return new UserStatusNotFoundException(Map.of("userStatusId", userStatusId));
    }

    public static UserStatusNotFoundException byUserId(UUID userId) {
        return new UserStatusNotFoundException(Map.of("userId", userId));
    }
}