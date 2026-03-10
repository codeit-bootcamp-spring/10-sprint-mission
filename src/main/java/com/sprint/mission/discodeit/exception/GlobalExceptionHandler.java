package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
    log.warn("Not Found Exception: {}", e.getMessage());
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), e.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
    log.warn("Bad Request Exception: {}", e.getMessage());
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), e.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
    log.warn("Illegal State Exception: {}", e.getMessage());
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), e.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException e) {
    log.warn("File Size Exceeded: {}", e.getMessage());
    HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), "업로드 가능한 파일 용량을 초과하였습니다."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
    log.error("Unhandled Exception Occurred!", e);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(status.value(), "서버 내부 오류가 발생하였습니다."));
  }
}
