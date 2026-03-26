package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  //커스텀 예외 적용
  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.warn(
        "비즈니스 예외 발생 - code={}, message={}, details={}",
        e.getErrorCode().getCode(),
        e.getMessage(),
        e.getDetails());

    return ResponseEntity.status(e.getErrorCode().getStatus()).body(ErrorResponse.fromException(e));
  }
  // 일반 예외 적용
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("서버 내부 예외 발생", e);

    return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.fromInternalServerError(e));
  }
}
