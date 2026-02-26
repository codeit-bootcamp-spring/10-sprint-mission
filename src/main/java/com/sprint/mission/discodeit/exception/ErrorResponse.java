package com.sprint.mission.discodeit.exception;

public record ErrorResponse(
    String satusCode,
    String message
) {

}
