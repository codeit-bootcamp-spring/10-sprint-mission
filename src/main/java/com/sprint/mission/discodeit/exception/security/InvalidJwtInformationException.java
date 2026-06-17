package com.sprint.mission.discodeit.exception.security;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidJwtInformationException extends SecurityException {

    public InvalidJwtInformationException() {
        super(ErrorCode.INVALID_JWT_INFORMATION, "message", "Invalid JWT Information");
    }
}
