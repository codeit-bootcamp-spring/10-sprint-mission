package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidPasswordException extends AuthException {

    public InvalidPasswordException(String message) {
        super(ErrorCode.INVALID_PASSWORD, message);
    }

    public InvalidPasswordException(String message, Map<String, Object> details) {
        super(ErrorCode.INVALID_PASSWORD, message, details);
    }
}
