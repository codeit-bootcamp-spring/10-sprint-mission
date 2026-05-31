package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class InternalServiceException extends DiscodeitException {

    public InternalServiceException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR, Instant.now(), Map.of());
    }
}
