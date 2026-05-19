package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class FieldNotValidException extends DiscodeitException {

    public FieldNotValidException(String field) {
        super(ErrorCode.FIELD_NOT_VALID, Instant.now(), Map.of("field", field));
    }
}
