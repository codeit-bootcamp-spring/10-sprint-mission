package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

  private final int statusCode;

  public BusinessLogicException(ExceptionCode e) {
    super(e.getMessage());
    this.statusCode = e.getStatusCode();
  }
}
