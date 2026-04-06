package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.error("DiscodeitException occurred: [{}], message: {}, details: {}",
        e.getErrorCode().getCode(), e.getMessage(), e.getDetails());

    ErrorResponse response = ErrorResponse.of(e);
    return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("Validation failed: {}", e.getMessage());

    Map<String, Object> details = new HashMap<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      details.put(error.getField(), error.getDefaultMessage());
    }

    DiscodeitException wrapper = new DiscodeitException(
        "Validation failed for some fields",
        ErrorCode.INVALID_INPUT_VALUE,
        details
    );

    return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
        .body(ErrorResponse.of(wrapper));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("IllegalArgumentException occurred: {}", e.getMessage());

    ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,
        e.getClass().getSimpleName());
    return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus()).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
    log.error("Internal server error occurred", e);

    ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR,
        e.getClass().getSimpleName());
    return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
  }
}
