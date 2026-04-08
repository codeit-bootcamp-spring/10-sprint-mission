package com.sprint.mission.discodeit.exception.global;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidInputException extends DiscodeitException {

    public InvalidInputException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
