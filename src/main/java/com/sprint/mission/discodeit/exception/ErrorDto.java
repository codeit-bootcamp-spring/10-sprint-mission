package com.sprint.mission.discodeit.exception;

import java.time.Instant;

public record ErrorDto(
        int status,
        String error,
        String message,
        Instant time
) {
    public static ErrorDto of(int status, String error, String message) {
        return new ErrorDto(status, error, message, Instant.now());
    }

    public static ErrorDto of(ErrorCode code, String message) {
        String msg = (message == null || message.isBlank()) ? code.getDefaultMessage() : message;
        return of(code.getHttpStatus().value(), code.name(), msg);
    }

    public static ErrorDto of(ErrorCode code) {
        return of(code, null);
    }
}
