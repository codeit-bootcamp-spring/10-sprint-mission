package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidCredentialsException extends UserException {

	public InvalidCredentialsException(String username) {
		super(ErrorCode.INVALID_CREDENTIALS, Map.of("username", username));
	}
}