package com.sprint.mission.discodeit.exception.userStatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;

import java.util.Map;

public class UserStatusException extends DiscodeitException {
    protected UserStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected UserStatusException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
