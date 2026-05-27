package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public abstract class DiscodeitException extends RuntimeException{
    private final Instant timestamp;
    private final ErrorCode errorCode;
    // 예외 발생 상황에 대한 추가 정보를 저장하기 위한 속성
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }
}
