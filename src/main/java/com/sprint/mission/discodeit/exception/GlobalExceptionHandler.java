package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), e.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), "서버 에러: " + e.getMessage()));
  }
}
