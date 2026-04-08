package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentSaveException extends BinaryContentException {

    public BinaryContentSaveException(UUID binaryContentId) {
        super(
                ErrorCode.BINARY_CONTENT_SAVE_FAILED,
                Map.of("binaryContentId", String.valueOf(binaryContentId))
        );
    }

    public BinaryContentSaveException(UUID binaryContentId, Throwable cause) {
        super(
                ErrorCode.BINARY_CONTENT_SAVE_FAILED,
                Map.of("binaryContentId", String.valueOf(binaryContentId)),
                cause
        );
    }

    public BinaryContentSaveException(String fileName, Throwable cause) {
        super(
                ErrorCode.BINARY_CONTENT_SAVE_FAILED,
                Map.of("fileName", String.valueOf(fileName)),
                cause
        );
    }
}
