package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("{}", e.getMessage(), e);

    ErrorResponse errorResponse = ErrorResponse.of(500, e);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String message = e.getFieldErrors().stream()
        .findFirst()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .orElse(null);
    log.warn("{}", message, e);

    ErrorResponse errorResponse = ErrorResponse.of(
        400, e.getClass().getSimpleName(), e.getBindingResult());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    String message = e.getConstraintViolations().stream()
        .findFirst()
        .map(ConstraintViolation::getMessage)
        .orElse(null);
    log.warn("{}", message, e);

    ErrorResponse errorResponse = ErrorResponse.of(
        400, e.getClass().getSimpleName(), e.getConstraintViolations());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleBusinessLogicException(DiscodeitException e) {
    int status = switch (e.getClass().getSimpleName()) {
      case "UserNotFoundException", "UserStatusNotFoundException", "ChannelNotFoundException",
           "ReadStatusNotFoundException", "MessageNotFoundException",
           "BinaryContentNotFoundException" -> 404;
      default -> 400;
    };
    log.warn("{}", e.getMessage(), e);

    ErrorResponse errorResponse = ErrorResponse.of(status, e);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }
}
