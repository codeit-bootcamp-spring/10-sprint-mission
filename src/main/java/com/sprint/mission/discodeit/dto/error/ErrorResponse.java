package com.sprint.mission.discodeit.dto.error;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message
) {
    public static ErrorResponse from(ErrorCode errorCode, String messageOverride) {
        String message = (messageOverride != null && !messageOverride.isBlank())
                ? messageOverride
                : errorCode.getMessage();

        return new ErrorResponse(
                Instant.now(),
                errorCode.getHttpStatus().value(),
                errorCode.getHttpStatus().getReasonPhrase(),
                errorCode.name(),
                message
        );
    }
}
