package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentReadException extends BinaryContentException {

    public BinaryContentReadException(UUID binaryContentId) {
        super(
                ErrorCode.BINARY_CONTENT_READ_FAILED,
                Map.of("binaryContentId", String.valueOf(binaryContentId))
        );
    }

    public BinaryContentReadException(UUID binaryContentId, Throwable cause) {
        super(
                ErrorCode.BINARY_CONTENT_READ_FAILED,
                Map.of("binaryContentId", String.valueOf(binaryContentId)),
                cause
        );
    }

    public BinaryContentReadException(String fileName, Throwable cause) {
        super(
                ErrorCode.BINARY_CONTENT_READ_FAILED,
                Map.of("fileName", String.valueOf(fileName)),
                cause
        );
    }
}
