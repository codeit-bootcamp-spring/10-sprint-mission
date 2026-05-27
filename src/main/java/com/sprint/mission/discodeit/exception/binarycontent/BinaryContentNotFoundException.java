package com.sprint.mission.discodeit.exception.binarycontent;

import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {

	public BinaryContentNotFoundException(UUID binaryContentId) {
		super(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("binaryContentId", binaryContentId));
	}
}