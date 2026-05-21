package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {
    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, String message) {
        super(message);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null ? details : new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public DiscodeitException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
        super(message, cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details != null ? details : new HashMap<>();
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
} 