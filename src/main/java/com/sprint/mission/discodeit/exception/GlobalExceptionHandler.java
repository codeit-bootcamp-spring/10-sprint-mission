package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception exception) {
    log.error("Unhandled exception: type={}, message={}",
        exception.getClass().getSimpleName(), exception.getMessage(), exception);

    ErrorResponse errorResponse = new ErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
    HttpStatus status = determineHttpStatus(exception);

    log.warn("Handled discodeit exception: status={}, code={}, message={}, details={}",
        status.value(), exception.getErrorCode(), exception.getMessage(), exception.getDetails());

    ErrorResponse response = new ErrorResponse(exception, status.value());
    return ResponseEntity
        .status(status)
        .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
    Map<String, Object> validationErrors = new HashMap<>();
    exception.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      validationErrors.put(fieldName, errorMessage);
    });

    log.warn("Validation failed: errors={}", validationErrors);

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        "VALIDATION_ERROR",
        "요청 값 검증에 실패했습니다.",
        validationErrors,
        exception.getClass().getSimpleName(),
        HttpStatus.BAD_REQUEST.value()
    );

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(response);
  }

  private HttpStatus determineHttpStatus(DiscodeitException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    return switch (errorCode) {
      case USER_NOT_FOUND, CHANNEL_NOT_FOUND, MESSAGE_NOT_FOUND, BINARY_CONTENT_NOT_FOUND,
          READ_STATUS_NOT_FOUND, USER_STATUS_NOT_FOUND -> HttpStatus.NOT_FOUND;
      case DUPLICATE_USER, DUPLICATE_READ_STATUS, DUPLICATE_USER_STATUS -> HttpStatus.CONFLICT;
      case INVALID_USER_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
      case PRIVATE_CHANNEL_UPDATE, INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
      case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
