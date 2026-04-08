package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidCredentialsException extends UserException {

    public InvalidCredentialsException(String username) {
        super(ErrorCode.INVALID_CREDENTIALS, Map.of("username", username));
    }
}