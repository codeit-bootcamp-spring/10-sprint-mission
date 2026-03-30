package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessLogicException extends RuntimeException {

  private HttpStatus statusCode;

  public BusinessLogicException(ExceptionCode e, Object... exceptionValues) {
    super(String.format(e.getMessage(), exceptionValues));
    this.statusCode = e.getStatusCode();
  }

  public String getMessage() {
    return super.getMessage();
  }
}
