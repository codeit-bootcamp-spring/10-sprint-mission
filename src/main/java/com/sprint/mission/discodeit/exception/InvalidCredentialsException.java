package com.sprint.mission.discodeit.exception;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }

    public InvalidCredentialsException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS,message);
    }
}
