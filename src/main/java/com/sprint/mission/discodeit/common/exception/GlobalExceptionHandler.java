package com.sprint.mission.discodeit.common.exception;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.warn("[{}] {}", e.getClass().getSimpleName(), e.getMessage());
    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(new ErrorResponse(
            e.getErrorCode().getStatus().value(),    // status
            e.getClass().getSimpleName(),            // exceptionType
            e.getErrorCode().name(),                 // code
            e.getMessage(),
            e.getDetails()
        ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleNotValidException(MethodArgumentNotValidException e) {
    log.warn("유효하지 않은 값 : {}", e.getMessage());

    Map<String, Object> details = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                .orElse("유효하지 않은 값입니다."),
            (existing, replacement) -> existing

        ));

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
            400,
            e.getClass().getSimpleName(),
            "VALIDATION_FAILED",
            "유효하지 않은 값이 존재합니다.",
            details
        ));
  }

  @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {

    log.warn("잘못된 요청 : {}", e.getMessage());

    String message = e.getMessage();
    if (e instanceof MethodArgumentTypeMismatchException) {
      message = "입력값의 형식이 올바르지 않습니다.";
    }
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(
            400,
            e.getClass().getSimpleName(),
            "BAD_REQUEST",
            message,
            Map.of()
        ));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {

    log.warn("데이터 없음 : {}", e.getMessage());

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(
            404,
            e.getClass().getSimpleName(),
            "NOT_FOUND",
            "해당 데이터를 찾을 수 없습니다.",
            Map.of()
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleServerError(Exception e) {

    log.error("서버 오류 발생 : {}", e.getMessage(), e);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(
            500,
            e.getClass().getSimpleName(),
            "INTERNAL_SERVER_ERROR",
            "서버 오류가 발생했습니다.",
            Map.of()
        ));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
    log.warn("🚫 Access denied: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("접근 권한이 없습니다.");
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
    log.error("❌ Runtime error: {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage() != null ? e.getMessage() : "잘못된 요청입니다.");
  }


}
