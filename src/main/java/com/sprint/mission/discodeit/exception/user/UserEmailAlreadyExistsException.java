package com.sprint.mission.discodeit.exception.user;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserEmailAlreadyExistsException extends UserException {

	public UserEmailAlreadyExistsException(String email) {
		super(ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", email));
	}
}