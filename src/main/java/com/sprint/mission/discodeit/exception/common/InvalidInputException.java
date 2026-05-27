package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidInputException extends CommonException {

    public InvalidInputException(String key, Object value) {
        super(ErrorCode.INVALID_INPUT, key, value);
    }
}
