package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleBusinessLogicException(DiscodeitException e) {
    log.warn("비즈니스 예외 발생 - code: {}, message: {}, details: {}",
        e.getErrorCode().name(), e.getMessage(), e.getDetails(), e);
    return ResponseEntity
        .status(e.getErrorCode().getStatusCode())
        .body(ErrorResponse.of(
                e.getTimestamp(),
                e.getErrorCode().name(),
                e.getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                e.getErrorCode().getStatusCode()
            )
        );
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("DTO 유효성 검증 실패", e);
    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(
        fieldError -> details.put(fieldError.getField(), fieldError.getDefaultMessage())
    );
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(
            Instant.now(),
            "INVALID_INPUT",
            "입력값이 유효하지 않습니다.",
            details,
            e.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
        ));
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException e) {
    log.warn("Parameter 유효성 검증 실패", e);
    Map<String, Object> details = new HashMap<>();
    e.getConstraintViolations().forEach(violation -> {
      String propertyPath = violation.getPropertyPath().toString();
      details.put(propertyPath, violation.getMessage());
    });
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(
            Instant.now(),
            "INVALID_PARAMETER",
            "요청 파라미터가 유효하지 않습니다.",
            details,
            e.getClass().getSimpleName(),
            HttpStatus.BAD_REQUEST.value()
        ));
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 서버 내부 오류 발생", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(
            Instant.now(),
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.",
            Collections.emptyMap(),
            e.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        ));
  }
}
