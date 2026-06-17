package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MalformedJwtException extends JwtException {
    public MalformedJwtException() {
        super(ErrorCode.MALFORMED_JWT);
    }
}
