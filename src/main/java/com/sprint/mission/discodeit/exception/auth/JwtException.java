package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class JwtException extends DiscodeitException {
    public JwtException(ErrorCode errorCode) {
        super(errorCode, Map.of());
    }

    public JwtException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
