package com.sprint.mission.discodeit.common.exception;

public record ErrorResponse(
        String message,
        int statusCode
) {
}
