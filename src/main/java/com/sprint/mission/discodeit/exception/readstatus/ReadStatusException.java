package com.sprint.mission.discodeit.exception.readstatus;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusException extends DiscodeitException {

	public ReadStatusException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ReadStatusException(ErrorCode errorCode, Map<String, Object> details) {
		super(errorCode, details);
	}

	public ReadStatusException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
		super(errorCode, details, cause);
	}
}