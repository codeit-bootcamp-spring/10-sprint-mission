package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final Instant timestamp;
    private final String code;
    private final String message;
    private final Map<String, Object> details;
    private final String exceptionType;
    private final int status;

    private ErrorResponse(Instant timestamp, String code, String message,
        Map<String, Object> details,
        String exceptionType, int status) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.details = details;
        this.exceptionType = exceptionType;
        this.status = status;
    }

    public static ErrorResponse of(DiscodeitException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return new ErrorResponse(exception.getTimestamp(), errorCode.toString(),
            exception.getMessage(), exception.getDetails(), exception.getClass().getSimpleName(),
            errorCode.getStatus().value());
    }

}
