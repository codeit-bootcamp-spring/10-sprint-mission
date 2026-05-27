package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class BinaryContentReadFailedException extends BinaryContentException {

    public BinaryContentReadFailedException(UUID binaryContentId, Throwable e) {
        super(ErrorCode.BINARY_CONTENT_READ_FAILED, "binaryContentId", binaryContentId, e);
    }
}
