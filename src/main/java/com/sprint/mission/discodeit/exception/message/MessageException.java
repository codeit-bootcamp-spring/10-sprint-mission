package com.sprint.mission.discodeit.exception.message;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageException extends DiscodeitException {

	public MessageException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MessageException(ErrorCode errorCode, Map<String, Object> details) {
		super(errorCode, details);
	}

	public MessageException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
		super(errorCode, details, cause);
	}
}