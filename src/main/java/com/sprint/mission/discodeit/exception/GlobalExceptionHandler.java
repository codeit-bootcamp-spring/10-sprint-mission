package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    ErrorCode errorCode = e.getErrorCode();

    ErrorResponse response = ErrorResponse.builder()
            .timestamp(e.getTimestamp())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .details(e.getDetails())
            .exceptionType(e.getClass().getSimpleName())
            .status(errorCode.getStatus().value())
            .build();

    log.warn("예외 발생 {} : {}", response.getExceptionType(), response.getMessage());

    return ResponseEntity
            .status(errorCode.getStatus())
            .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .details(Map.of("reason", e.getMessage() != null ? e.getMessage() : "No message available"))
            .exceptionType(e.getClass().getSimpleName())
            .status(errorCode.getStatus().value())
            .build();

    log.error("서버 내부 오류 발생", e);

    return ResponseEntity
            .status(errorCode.getStatus())
            .body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

    // 상세 정보 수집
    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error ->
            details.put(error.getField(), error.getDefaultMessage())
    );

    // ErrorResponse 객체 생성
    ErrorResponse response = ErrorResponse.builder()
            .timestamp(Instant.now())
            .code(errorCode.getCode())
            .message("입력 데이터 검증에 실패했습니다.")
            .details(details)
            .exceptionType(e.getClass().getSimpleName())
            .status(errorCode.getStatus().value())
            .build();

    // 로그 기록
    log.warn("검증 실패: {}", details);

    return ResponseEntity
            .status(errorCode.getStatus())
            .body(response);
  }
}
