package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

import com.sprint.mission.discodeit.exception.base.DiscodeitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    HttpStatus status = e.getErrorCode().getStatus();

    ErrorResponse errorResponse = new ErrorResponse(
            e.getTimestamp(),
            e.getErrorCode().name(),
            e.getMessage(),
            e.getDetails(),
            e.getClass().getSimpleName(),
            status.value()
    );

    return ResponseEntity
            .status(status)
            .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            "INTERNAL_SERVER_ERROR",
            "Internal server error",
            Map.of(),
            e.getClass().getSimpleName(),
            status.value()
    );
    return ResponseEntity
            .status(status)
            .body(errorResponse);
  }
}
