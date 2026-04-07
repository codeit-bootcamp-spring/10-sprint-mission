package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidPasswordException extends AuthException {

    public InvalidPasswordException(Map<String, Object> details) {
        super(ErrorCode.PASSWORD_WRONG, details);
    }

}
