package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;

import java.util.Map;

public class BinaryContentException extends DiscodeitException {
    protected BinaryContentException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected BinaryContentException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
