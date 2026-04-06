package com.sprint.mission.discodeit.exception.global;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidInputException extends GlobalException {

    // 특정된 필드 오류
    public InvalidInputException(String field, String message) {
        super(ErrorCode.INVALID_INPUT_VALUE, Map.of("field", field, "reason", message));
    }

    // 특정되지 않은 필드 오류
    public InvalidInputException(String message) {
        super(ErrorCode.INVALID_INPUT_VALUE, Map.of("reason", message));
    }
}
