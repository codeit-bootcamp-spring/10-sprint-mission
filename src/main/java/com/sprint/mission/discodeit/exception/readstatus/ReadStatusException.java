package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ReadStatusException extends DiscodeitException {

    public ReadStatusException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ReadStatusException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public ReadStatusException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
