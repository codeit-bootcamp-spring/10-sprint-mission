package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserAlreadyExistException extends UserExeption{
    public UserAlreadyExistException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
