package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentDeleteException extends BinaryContentException {

    public BinaryContentDeleteException(UUID binaryContentId, Throwable cause) {
        super(
                ErrorCode.BINARY_CONTENT_DELETE_FAILED,
                Map.of("binaryContentId", String.valueOf(binaryContentId)),
                cause
        );
    }
}
