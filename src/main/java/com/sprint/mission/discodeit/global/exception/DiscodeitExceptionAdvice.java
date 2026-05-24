package com.sprint.mission.discodeit.global.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DiscodeitExceptionAdvice {

  // todo
  @ExceptionHandler(MethodArgumentNotValidException.class)
  private ResponseEntity<ErrorResponse> handleIncorrectInput(
      MethodArgumentNotValidException exception) {
    int status = exception.getStatusCode().value();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(status)
        .message(exception.getLocalizedMessage())
        .build();
    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(DiscodeitException.class)
  private ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
    int status = exception.getStatus();
    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(status)
        .exceptionType(exception.getExceptionType())
        .code(exception.getCode())
        .message(exception.getMessage())
        .details(exception.getDetails())
        .timestamp(exception.getTimestamp())
        .build();
    return ResponseEntity.status(status).body(errorResponse);
  }
}
