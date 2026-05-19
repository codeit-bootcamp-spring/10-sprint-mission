package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UsernameAlreadyExistsException extends UserException {

	public UsernameAlreadyExistsException(String username) {
		super(ErrorCode.USERNAME_ALREADY_EXISTS, Map.of("username", username));
	}
}