package com.sprint.mission.discodeit.common.exception.custom;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class APIException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detail;

    public APIException(ErrorCode errorCode, Object source) {
        this.errorCode = errorCode;
        this.detail = String.join(", ", errorCode.getMessage(), source.toString());
    }

    public APIException(ErrorCode errorCode) {
        this(errorCode, "");
    }
}
