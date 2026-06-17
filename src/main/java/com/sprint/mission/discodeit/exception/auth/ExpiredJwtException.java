package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ExpiredJwtException extends JwtException {
    public ExpiredJwtException() {
        super(ErrorCode.EXPIRED_JWT);
    }
}
