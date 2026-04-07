package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

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
  // MethodArgumentNotValidException 핸들러 추가.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
          MethodArgumentNotValidException e
  ) {
    Map<String, Object> details = new LinkedHashMap<>();

    Map<String, String> fieldErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    fieldError -> fieldError.getDefaultMessage() != null
                            ? fieldError.getDefaultMessage()
                            : "유효하지 않은 값입니다.",
                    (existing, replacement) -> existing,
                    LinkedHashMap::new
            ));

    details.put("fieldErrors", fieldErrors);

    log.warn("유효성 검증 실패 - errors={}", fieldErrors);

    return ResponseEntity.badRequest()
            .body(ErrorResponse.of(
                    "VALIDATION_ERROR",
                    "요청 데이터 검증에 실패했습니다.",
                    400,
                    e.getClass().getSimpleName(),
                    details
            ));
  }

  // 일반 예외 적용
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("잘못된 요청", e);

    return ResponseEntity.badRequest()
            .body(ErrorResponse.of(
                    "BAD_REQUEST",
                    e.getMessage(),
                    400,
                    e.getClass().getSimpleName(),
                    Map.of()
            ));
  }
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
    log.warn("리소스 없음", e);

    return ResponseEntity.status(404)
            .body(ErrorResponse.of(
                    "NOT_FOUND",
                    e.getMessage(),
                    404,
                    e.getClass().getSimpleName(),
                    Map.of()
            ));
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("서버 내부 예외 발생", e);

    return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.fromInternalServerError(e));
  }
}
