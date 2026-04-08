package com.sprint.mission.discodeit.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final Instant timestamp;
    private final String code;
    private final String message;
    private final Map<String, Object> details;
    private final String exceptionType;
    private final int status;

    @Builder
    public ErrorResponse(String code, String message, Map<String, Object> details, String exceptionType, int status) {
        this.timestamp = Instant.now();
        this.code = code;
        this.message = message;
        this.details = details != null ? details : Map.of();
        this.exceptionType = exceptionType;
        this.status = status;
    }
}