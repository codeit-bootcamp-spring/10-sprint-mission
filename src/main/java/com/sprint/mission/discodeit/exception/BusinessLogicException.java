package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {
    private final int status;
    private final String code;

    public BusinessLogicException(ErrorCode e) {
        super(e.getMessage());
        this.status = e.getStatus();
        this.code = e.name();
    }
}
