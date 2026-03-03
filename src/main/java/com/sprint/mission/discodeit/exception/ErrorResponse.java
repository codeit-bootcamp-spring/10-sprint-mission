package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import lombok.Getter;

@Getter
public class ErrorResponse {

  private final String code;
  private final String message;
  private final Instant timestamp;


  public ErrorResponse(String code, String message) {
    this.code = code;
    this.message = message;
    this.timestamp = Instant.now();
  }

  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
  }

  public static ErrorResponse of(ErrorCode errorCode, String message) {
    return new ErrorResponse(errorCode.getCode(), message);
  }
}
