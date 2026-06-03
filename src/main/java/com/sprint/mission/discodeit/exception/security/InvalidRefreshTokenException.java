package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidRefreshTokenException extends SecurityException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh Token", "Invalid");
    }

    public InvalidRefreshTokenException(Throwable e) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh Token", "Invalid", e);
    }
}
