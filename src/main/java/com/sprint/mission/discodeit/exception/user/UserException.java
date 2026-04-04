package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class UserException extends DiscodeitException {

    protected UserException(ErrorCode errorCode) {
        super(errorCode, Instant.now(), Map.of());
    }

    protected UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, Instant.now(), details == null ? Map.of() : details);
    }
}
