package com.sprint.mission.discodeit.exception.auth;

import java.util.Collections;
import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidRefreshTokenException extends DiscodeitException {

	public InvalidRefreshTokenException() {
		super(ErrorCode.INVALID_REFRESH_TOKEN, Collections.emptyMap());
	}

	public InvalidRefreshTokenException(Map<String, Object> details) {
		super(ErrorCode.INVALID_REFRESH_TOKEN, details);
	}

	public InvalidRefreshTokenException(Throwable cause) {
		super(ErrorCode.INVALID_REFRESH_TOKEN, Collections.emptyMap(), cause);
	}
}
