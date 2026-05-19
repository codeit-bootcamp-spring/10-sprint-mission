package com.sprint.mission.discodeit.exception.userstatus;

import java.util.Map;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;

public class UserStatusException extends UserException {

	public UserStatusException(ErrorCode errorCode) {
		super(errorCode);
	}

	public UserStatusException(ErrorCode errorCode, Map<String, Object> details) {
		super(errorCode, details);
	}

	public UserStatusException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
		super(errorCode, details, cause);
	}
}