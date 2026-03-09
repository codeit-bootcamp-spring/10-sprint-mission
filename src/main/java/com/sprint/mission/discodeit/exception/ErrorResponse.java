package com.sprint.mission.discodeit.exception;

public record ErrorResponse(
    Integer code,
    String message
) {

  public static ErrorResponse of(Integer code, String message) {
    return new ErrorResponse(code, message);
  }
}
