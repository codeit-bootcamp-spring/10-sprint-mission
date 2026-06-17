package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidJwtTokenException extends SecurityException {

    public InvalidJwtTokenException() {
        super(ErrorCode.INVALID_JWT_TOKEN, "message", "Invalid Token");
    }

    public InvalidJwtTokenException(String message) {
        super(ErrorCode.INVALID_JWT_TOKEN, "message", message);
    }

    public InvalidJwtTokenException(Throwable e) {
        super(ErrorCode.INVALID_JWT_TOKEN, "message", "Invalid Token", e);
    }

    public InvalidJwtTokenException(String message, Throwable e) {
        super(ErrorCode.INVALID_JWT_TOKEN, "message", message, e);
    }
}
