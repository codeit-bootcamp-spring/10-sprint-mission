package com.sprint.mission.discodeit.exception.login;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class InvalidPasswordException extends LoginException{
    public InvalidPasswordException(String username){
        super(ErrorCode.INVALID_PASSWORD, Map.of("username", username));
    }
}
