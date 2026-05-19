package com.sprint.mission.discodeit.exception.binarycontent;

import java.util.Map;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentException extends DiscodeitException {

	public BinaryContentException(ErrorCode errorCode) {
		super(errorCode);
	}

	public BinaryContentException(ErrorCode errorCode, Map<String, Object> details) {
		super(errorCode, details);
	}

	public BinaryContentException(ErrorCode errorCode, Map<String, Object> details,
		Throwable cause) {
		super(errorCode, details, cause);
	}
}