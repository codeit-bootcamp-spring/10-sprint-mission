package com.sprint.mission.discodeit.exception.event;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class EventDeserializationFailedException extends EventException {

    public EventDeserializationFailedException(Object value, Throwable e) {
        super(ErrorCode.EVENT_SERIALIZATION_FAILED, "message", value, e);
    }

    public EventDeserializationFailedException(String key, Object value, Throwable e) {
        super(ErrorCode.EVENT_SERIALIZATION_FAILED, key, value, e);
    }
}
