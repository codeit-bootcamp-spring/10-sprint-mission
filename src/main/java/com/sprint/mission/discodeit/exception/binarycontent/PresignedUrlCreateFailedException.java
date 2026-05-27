package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class PresignedUrlCreateFailedException extends BinaryContentException {

    public PresignedUrlCreateFailedException(UUID binaryContentId, Throwable e) {
        super(ErrorCode.PRESIGNED_URL_CREATE_FAILED, "binaryContentId", binaryContentId, e);
    }
}
