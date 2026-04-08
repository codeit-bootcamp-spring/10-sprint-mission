package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DiscodeitException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DiscodeitException(ErrorCode errorCode, String message) {
        this(errorCode, message, Map.of());
    }

    public DiscodeitException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public DiscodeitException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(message, cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
