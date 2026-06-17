package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public DiscodeitException(ErrorCode errorCode,
      Map<String, Object> details, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = (details != null) ? Map.copyOf(details) : Map.of();
  }

  public DiscodeitException(ErrorCode errorCode) {
    this(errorCode, null, null);
  }

  public DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    this(errorCode, details, null);
  }

  public DiscodeitException(ErrorCode errorCode, Throwable cause) {
    this(errorCode, null, cause);
  }
}
