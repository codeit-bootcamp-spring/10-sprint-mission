package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public class RequestNullException extends DiscodeitException {

    public RequestNullException() {
        super(ErrorCode.REQUEST_NOT_VALID, Instant.now(), Map.of());
    }


}
