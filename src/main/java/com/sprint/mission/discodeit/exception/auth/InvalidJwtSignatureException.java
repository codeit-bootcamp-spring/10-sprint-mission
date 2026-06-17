package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidJwtSignatureException extends JwtException {
    public InvalidJwtSignatureException() {
        super(ErrorCode.INVALID_JWT_SIGNATURE);
    }
}
