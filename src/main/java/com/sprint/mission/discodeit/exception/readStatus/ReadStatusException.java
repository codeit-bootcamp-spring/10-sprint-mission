package com.sprint.mission.discodeit.exception.readStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;

import java.util.Map;

public class ReadStatusException extends DiscodeitException {
    protected ReadStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ReadStatusException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
