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

    // 커스텀 예외를 받고 반환함.
    public static ErrorResponse of(DiscodeitException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return new ErrorResponse(exception.getTimestamp(), errorCode.toString(),
            exception.getMessage(), exception.getDetails(), exception.getClass().getSimpleName(),
            errorCode.getStatus().value());
    }

    // 커스텀 예외를 제외한 다른 예외일 경우
    public static ErrorResponse of(Exception e, ErrorCode errorCode, Map<String, Object> details) {
        return new ErrorResponse(Instant.now(), errorCode.toString(),
            errorCode.getMessage(), details, e.getClass().getSimpleName(),
            errorCode.getStatus().value());
    }

}
