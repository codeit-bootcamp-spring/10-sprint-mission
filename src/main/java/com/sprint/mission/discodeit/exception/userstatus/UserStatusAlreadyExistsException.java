package com.sprint.mission.discodeit.exception.userstatus;

import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserStatusAlreadyExistsException extends UserStatusException {

	public UserStatusAlreadyExistsException(UUID userId) {
		super(ErrorCode.USER_STATUS_ALREADY_EXISTS, Map.of("userId", userId));
	}
}