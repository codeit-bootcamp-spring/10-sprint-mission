package com.sprint.mission.discodeit.common.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponse {

  private final Instant timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;

  public ErrorResponse(int status, String exceptionType, String code,
      String message, Map<String, Object> details) {
    this.timestamp = Instant.now();
    this.status = status;
    this.exceptionType = exceptionType;
    this.code = code;
    this.message = message;
    this.details = details;
  }
}