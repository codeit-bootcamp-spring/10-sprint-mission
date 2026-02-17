package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessLogicException(ErrorCode errorCode, String messageOverride) {
        super(messageOverride);
        this.errorCode = errorCode;
    }
}
