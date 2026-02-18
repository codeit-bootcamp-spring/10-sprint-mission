package com.sprint.mission.discodeit.exception;

public record ErrorResponse(
	String code,
	String message
) {
}
