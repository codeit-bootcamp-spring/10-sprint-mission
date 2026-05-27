package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class NoChangeValueException extends CommonException {

    public NoChangeValueException(String key, Object value) {
        super(ErrorCode.NO_CHANGE_VALUE, key, value);
    }

}
