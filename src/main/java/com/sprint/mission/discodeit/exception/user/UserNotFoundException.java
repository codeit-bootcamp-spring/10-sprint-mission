package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String key, Object value) {
        super(ErrorCode.USER_NOT_FOUND, key, value);
    }
}
