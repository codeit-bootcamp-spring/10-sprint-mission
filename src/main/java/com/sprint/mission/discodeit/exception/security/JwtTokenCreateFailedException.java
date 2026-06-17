package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class JwtTokenCreateFailedException extends SecurityException {

    public JwtTokenCreateFailedException(String message, Throwable e) {
        super(ErrorCode.JWT_TOKEN_CREATE_FAILED, "message", message, e);
    }
}
