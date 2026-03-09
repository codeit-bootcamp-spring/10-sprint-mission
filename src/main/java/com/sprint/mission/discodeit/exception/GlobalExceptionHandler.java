package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<?> handleBusinessLogicException(BusinessLogicException e) {
    return ResponseEntity
        .status(e.getStatusCode())
        .body(ErrorResponse.of(e.getStatusCode(), e.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<?> handleException(Exception e) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
  }
}
