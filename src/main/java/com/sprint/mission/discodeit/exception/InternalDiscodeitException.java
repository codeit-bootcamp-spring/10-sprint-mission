package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class InternalDiscodeitException extends DiscodeitException {

    public InternalDiscodeitException(String message) {
        super(ErrorCode.INTERNAL_ERROR, message);
    }

    public InternalDiscodeitException(String message, Map<String, Object> details) {
        super(ErrorCode.INTERNAL_ERROR, message, details);
    }

    public InternalDiscodeitException(
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(ErrorCode.INTERNAL_ERROR, message, details, cause);
    }
}
