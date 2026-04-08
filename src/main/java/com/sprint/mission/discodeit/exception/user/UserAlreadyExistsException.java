package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserAlreadyExistsException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details
    ) {
        super(errorCode, message, details);
    }
}
