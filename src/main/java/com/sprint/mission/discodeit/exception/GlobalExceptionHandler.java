package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 기본 에러
  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Server Error: ", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(e));
  }

  @ExceptionHandler(value = DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(DiscodeitException e) {
    log.warn("[DiscodeitException] Status: {}, Code: {}, Message: {}, Details: {}",
        e.getErrorCode().getStatus(), e.getErrorCode().getCode(), e.getMessage(), e.getDetails());
    return ResponseEntity.status(HttpStatus.valueOf(e.getErrorCode().getStatus()))
        .body(ErrorResponse.of(e));
  }

  //파라미터
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("[MethodArgumentNotValidException] Field Errors: {}",
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": "
                + fieldError.getDefaultMessage())
            .toList());
    return ResponseEntity.badRequest().body(ErrorResponse.of(e));
  }

  //경로
  @ExceptionHandler(value = ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    log.warn("[ConstraintViolationException] Violation Errors: {}",
        e.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": "
                + violation.getMessage())
            .toList());
    return ResponseEntity.badRequest().body(ErrorResponse.of(e));
  }

  //시큐리티
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    log.warn("[AccessDeniedException] Errors: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse.of(e));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
    log.warn("[AuthenticationException] Errors: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.of(e));
  }
}