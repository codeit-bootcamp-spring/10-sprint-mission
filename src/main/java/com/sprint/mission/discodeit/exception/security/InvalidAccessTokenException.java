package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidAccessTokenException extends SecurityException {

    public InvalidAccessTokenException() {
        super(ErrorCode.INVALID_ACCESS_TOKEN, "message", "Invalid Access Token");
    }

    public InvalidAccessTokenException(Throwable e) {
        super(ErrorCode.INVALID_ACCESS_TOKEN, "message", "Invalid Access Token", e);
    }
}
