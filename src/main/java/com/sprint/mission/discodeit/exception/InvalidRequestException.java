package com.sprint.mission.discodeit.exception;

import java.util.Map;

public class InvalidRequestException extends DiscodeitException {

    public InvalidRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }

    public InvalidRequestException(String message, Map<String, Object> details) {
        super(ErrorCode.INVALID_REQUEST, message, details);
    }

    public InvalidRequestException(String message, Map<String, Object> details, Throwable cause) {
        super(ErrorCode.INVALID_REQUEST, message, details, cause);
    }
}
