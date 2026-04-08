package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class AuthException extends DiscodeitException {

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public AuthException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
