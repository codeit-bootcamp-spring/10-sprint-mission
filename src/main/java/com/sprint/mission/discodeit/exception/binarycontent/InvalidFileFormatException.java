package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidFileFormatException extends BinaryContentException {
    public InvalidFileFormatException(String contentType) {
        super(ErrorCode.INVALID_FILE_FORMAT, Map.of("contentType", contentType != null ? contentType : "unknown"));
    }
}
