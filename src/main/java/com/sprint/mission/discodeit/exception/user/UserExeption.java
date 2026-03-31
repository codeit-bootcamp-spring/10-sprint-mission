package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserExeption extends DiscodeitException {
    public UserExeption(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
