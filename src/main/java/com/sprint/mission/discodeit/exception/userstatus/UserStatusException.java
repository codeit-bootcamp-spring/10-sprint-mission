package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusException extends DiscodeitException {

    public UserStatusException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserStatusException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public UserStatusException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
