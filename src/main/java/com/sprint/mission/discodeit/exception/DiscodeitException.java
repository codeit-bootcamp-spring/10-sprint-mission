package com.sprint.mission.discodeit.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscodeitException extends RuntimeException {
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap());
    }

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Collections.unmodifiableMap(new HashMap<>(details));
    }

    public DiscodeitException(String message, ErrorCode errorCode) {
        this(message, errorCode, Collections.emptyMap());
    }

    public DiscodeitException(String message, ErrorCode errorCode, Map<String, Object> details) {
        super(message);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Collections.unmodifiableMap(new HashMap<>(details));
    }
}
