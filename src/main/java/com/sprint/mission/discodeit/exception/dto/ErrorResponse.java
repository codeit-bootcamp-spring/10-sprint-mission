package com.sprint.mission.discodeit.exception.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ErrorResponse {

  private final int status;
  private final String exceptionType;
  private final String code;
  private final String message;
  private final Map<String, String> details;
  private final LocalDateTime timestamp;

  @Builder
  private ErrorResponse(Instant timestamp, String code, String message,
      Map<String, String> details, String exceptionType, int status) {
    this.timestamp = toLocalDateTime(timestamp);
    this.code = code;
    this.message = message;
    this.details = details;
    this.exceptionType = exceptionType;
    this.status = status;
  }

  private LocalDateTime toLocalDateTime(Instant timestamp) {
    return LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
  }
}
