package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<?> handleBusinessLogicException(DiscodeitException e) {
    log.error("비즈니스 예외 발생 - code: {}, message: {}, details: {}",
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
  public ResponseEntity<?> handleException(Exception e) {
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
