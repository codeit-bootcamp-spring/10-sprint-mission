package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timeStamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

  public static ErrorResponse of(
      Instant timeStamp,
      String code,
      String message,
      Map<String, Object> details,
      String exceptionType,
      int status
  ) {
    return new ErrorResponse(timeStamp, code, message, details, exceptionType, status);
  }
}
