package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;

import java.util.Map;

public class AuthenticationException extends DiscodeitException {
    public AuthenticationException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }
}
