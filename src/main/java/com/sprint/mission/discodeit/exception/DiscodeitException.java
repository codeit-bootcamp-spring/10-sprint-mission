package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }
    public DiscodeitException(ErrorCode errorCode, Map<String, Object> details, Throwable cause ) {
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details;
    }

    public DiscodeitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = Map.of();
    }
}
