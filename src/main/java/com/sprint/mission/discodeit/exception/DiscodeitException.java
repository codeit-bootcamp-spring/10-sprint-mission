package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@Getter
public abstract class DiscodeitException extends RuntimeException { // abstract이 좋을지?
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    // 기본 생성자
    protected DiscodeitException(ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap());
    }

    // 상세 정보 생성자
    protected DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage()); // RuntimeException에 메시지 전달
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Collections.unmodifiableMap(details);
    }

}
