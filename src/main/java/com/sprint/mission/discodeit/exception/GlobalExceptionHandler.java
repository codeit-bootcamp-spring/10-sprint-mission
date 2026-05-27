package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.error("Business Exception occurred: [{}] {}", e.getErrorCode().getCode(), e.getMessage(),
        e); // 로그에 예외 객체를 파라미터로 넘겨 상세 원인 기록

    // 응답 객체 생성
    ErrorResponse response = ErrorResponse.of(e);

    // ErrorCode에 정의된 HTTP 상태 코드로 응답 전송
    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleRemainingException(Exception e) {
    log.error("Unexpected Internal Server Error: ", e);

    // ErrorCode에 정의한 INTERNAL_SERVER_ERROR 활용
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        null,
        e.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    return ResponseEntity.status(500).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException e) {
    // 어떤 필드에서 어떤 에러가 났는지 상세 정보(details) 생성
    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error ->
        details.put(error.getField(), error.getDefaultMessage())
    );

    log.warn("Validation failed: {}", details);

    // ErrorCode에 정의한 INVALID_INPUT_VALUE 활용
    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        details,
        e.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  // 메서드 시큐리티 예외를 가로채서 403으로 반환
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());

    ErrorCode errorCode = ErrorCode.GLOBAL_ACCESS_DENIED;

    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        null,
        ex.getClass().getSimpleName(),
        errorCode.getStatus().value()
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }
}
