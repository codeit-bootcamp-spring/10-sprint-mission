package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentStorageInitFailedException extends BinaryContentException {

    public BinaryContentStorageInitFailedException(Throwable e) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_INIT_FAILED, "Storage Init", "Failed", e);
    }
}
