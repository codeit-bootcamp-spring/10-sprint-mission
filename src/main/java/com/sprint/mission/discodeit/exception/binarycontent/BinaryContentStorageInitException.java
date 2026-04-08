package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.nio.file.Path;
import java.util.Map;

public class BinaryContentStorageInitException extends BinaryContentException {

    public BinaryContentStorageInitException(Path rootPath, Throwable cause) {
        super(
                ErrorCode.BINARY_CONTENT_STORAGE_INIT_FAILED,
                Map.of("rootPath", String.valueOf(rootPath)),
                cause
        );
    }
}
