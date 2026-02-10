package com.sprint.mission.discodeit.response;

public record ErrorResponse(
        ErrorCode errorCode,
        String message
) {
}
