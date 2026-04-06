package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserStatusException {
    public UserStatusNotFoundException(UUID userStatusId) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("userStatusId", userStatusId));
    }

    public static UserStatusNotFoundException byUserId(UUID userId) {
        return new UserStatusNotFoundException(Map.of("userId", userId));
    }

    private UserStatusNotFoundException(Map<String, Object> details) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, details);
    }
}
