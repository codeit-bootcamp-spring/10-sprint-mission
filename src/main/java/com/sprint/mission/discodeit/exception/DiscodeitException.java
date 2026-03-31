package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class DiscodeitException extends RuntimeException {

  private final Instant timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  protected DiscodeitException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = Map.of();
  }

  protected DiscodeitException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : details;
  }

  protected DiscodeitException(ErrorCode errorCode, String messageOverride,
      Map<String, Object> details) {
    super(messageOverride);
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details == null ? Map.of() : details;
  }

}
