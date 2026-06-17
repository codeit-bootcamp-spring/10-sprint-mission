package com.sprint.mission.discodeit.exception.event;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class EventSerializationFailedException extends EventException {

    public EventSerializationFailedException(Object value, Throwable e) {
        super(ErrorCode.EVENT_SERIALIZATION_FAILED, "message", value, e);
    }

    public EventSerializationFailedException(String key, Object value, Throwable e) {
        super(ErrorCode.EVENT_SERIALIZATION_FAILED, key, value, e);
    }
}
