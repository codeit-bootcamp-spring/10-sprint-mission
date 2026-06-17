package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class JwtInformationNotFoundException extends SecurityException {

    public JwtInformationNotFoundException(String message) {
        super(ErrorCode.JWT_INFORMATION_NOT_FOUND, "message", message);
    }
}
