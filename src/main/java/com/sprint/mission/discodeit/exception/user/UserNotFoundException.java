package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }

    public UserNotFoundException(String message, Map<String, Object> details) {
        super(ErrorCode.USER_NOT_FOUND, message, details);
    }
}
