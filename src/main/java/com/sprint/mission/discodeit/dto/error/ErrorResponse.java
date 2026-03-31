package com.sprint.mission.discodeit.dto.error;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String code,
    String message,
    String exceptionType,
    Map<String, Object> details
) {

  public static ErrorResponse from(
      ErrorCode errorCode,
      String messageOverride,
      String exceptionType,
      Map<String, Object> details
  ) {
    String message = (messageOverride != null && !messageOverride.isBlank())
        ? messageOverride
        : errorCode.getMessage();

    return new ErrorResponse(
        Instant.now(),
        errorCode.getHttpStatus().value(),
        errorCode.getHttpStatus().getReasonPhrase(),
        errorCode.name(),
        message,
        exceptionType,
        details == null ? Map.of() : details
    );
  }

  public static ErrorResponse from(
      ErrorCode errorCode,
      String messageOverride,
      String exceptionType
  ) {
    return from(errorCode, messageOverride, exceptionType, Map.of());
  }
}
