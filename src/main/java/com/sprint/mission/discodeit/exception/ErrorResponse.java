package com.sprint.mission.discodeit.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private Instant timestamp;
    private String code;
    private String message;
    private Map<String, Object> details;
    private String exceptionType;
    private int status;

    private ErrorResponse(DiscodeitException e) {
        this.timestamp = e.getTimestamp();
        this.code = e.getErrorCode().getCode();
        this.message = e.getMessage();
        this.details = e.getDetails();
        this.exceptionType = e.getClass().getSimpleName();
        this.status = e.getErrorCode().getStatus().value();
    }

    private ErrorResponse(ErrorCode errorCode, String exceptionType) {
        this.timestamp = Instant.now();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.details = Map.of();
        this.exceptionType = exceptionType;
        this.status = errorCode.getStatus().value();
    }

    public static ErrorResponse of(DiscodeitException e) {
        return new ErrorResponse(e);
    }

    public static ErrorResponse of(ErrorCode errorCode, String exceptionType) {
        return new ErrorResponse(errorCode, exceptionType);
    }
}
