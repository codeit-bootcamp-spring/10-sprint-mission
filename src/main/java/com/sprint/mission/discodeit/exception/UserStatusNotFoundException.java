package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserStatusNotFoundException extends BusinessException {
    public UserStatusNotFoundException(UUID userStatusId) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, "UserStatus with id" + userStatusId + "not found");
    }

    public UserStatusNotFoundException(String message) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, message);
    }
}
