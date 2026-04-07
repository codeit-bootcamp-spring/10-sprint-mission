package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidBinaryContentException extends BinaryContentException{

    public InvalidBinaryContentException(String reason) {
        super(ErrorCode.INVALID_BINARY_CONTENT, Map.of("reason", reason));
    }
}
