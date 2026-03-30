package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    // ErrorCode에 정의한 INTERNAL_SERVER_ERROR 활용 (status: 500)
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
}
