package com.sprint.mission.discodeit.exception;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException exception) {
        ErrorCode code = exception.getErrorCode();

        if (code.getHttpStatus().is5xxServerError()) {
            log.error("[EXCEPTION] 서버 예외 응답: exceptionType={}, code={}, details={}",
                    exception.getClass().getSimpleName(),
                    code.name(),
                    exception.getDetails(),
                    exception);
        } else {
            log.warn("[EXCEPTION] 비즈니스 예외 응답: exceptionType={}, code={}, details={}",
                    exception.getClass().getSimpleName(),
                    code.name(),
                    exception.getDetails());
        }

        return ResponseEntity.status(code.getHttpStatus()).body(ErrorResponse.of(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception) {
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        Map<String, Object> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> details.put(error.getField(), error.getDefaultMessage()));

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(code.getMessage());

        log.warn("[EXCEPTION] 유효성 검증 실패: details={}", details);
        return ResponseEntity.status(code.getHttpStatus())
                .body(ErrorResponse.of(code, exception.getClass().getSimpleName(), message,
                        details));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        ErrorCode code = ErrorCode.BAD_REQUEST;

        log.warn("[EXCEPTION] 잘못된 요청 처리: message={}", exception.getMessage());
        return ResponseEntity.status(code.getHttpStatus())
                .body(ErrorResponse.of(
                        code,
                        exception.getClass().getSimpleName(),
                        exception.getMessage(),
                        Collections.emptyMap()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        ErrorCode code = ErrorCode.INTERNAL_ERROR;

        log.error("[EXCEPTION] 처리되지 않은 예외 발생", exception);
        return ResponseEntity.status(code.getHttpStatus())
                .body(ErrorResponse.of(
                        code,
                        exception.getClass().getSimpleName(),
                        code.getMessage(),
                        Collections.emptyMap()
                ));
    }
}
