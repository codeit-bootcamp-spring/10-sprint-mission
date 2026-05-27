package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public abstract class MessageException extends DiscodeitException {

    protected MessageException(ErrorCode errorCode, String key, Object value) {
        super(errorCode, key, value);
    }

    protected MessageException(ErrorCode errorCode, Map<String, Object> details, Throwable e) {
        super(errorCode, details, e);
    }
}
