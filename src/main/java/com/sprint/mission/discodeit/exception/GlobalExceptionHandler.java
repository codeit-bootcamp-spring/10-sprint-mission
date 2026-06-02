package com.sprint.mission.discodeit.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 예외
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException ex) {
        HttpStatus status = ex.getErrorCode().getStatus();

        if (status.is5xxServerError()) {
            log.error("Server Side Custom Error - {}: {} | Details: {} | StackTrace:",
                    ex.getClass().getSimpleName(), ex.getMessage(), ex.getDetails(), ex);
        } else {
            log.warn("Client Side Custom Error - {}: {} | Details: {}",
                    ex.getClass().getSimpleName(), ex.getMessage(), ex.getDetails());
        }

        return ResponseEntity.status(status).body(ErrorResponse.of(ex));
    }

    // Valid 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());

        String message = "입력값 검증에 실패하였습니다.";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(e, "VALIDATION_ERROR", message, status.value(), errors));
    }

    // URL 파라미터 예외
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());

        String fieldName = e.getName();
        String requiredType =
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("'%s' 파라미터의 타입이 올바르지 않습니다. (기대 타입: %s)", fieldName,
                requiredType);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> details = Map.of(
                "parameter", fieldName,
                "rejectedValue", e.getValue() != null ? e.getValue() : "null",
                "expectedType", requiredType
        );

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(e, "TYPE_MISMATCH", message, status.value(), details));
    }

    // JSON 파싱 예외
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());

        String message = "입력 형식이나 타입이 올바르지 않습니다.";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> details = new HashMap<>();

        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException target) {
            String fieldName = target.getPath().get(0).getFieldName();
            Object rejectedValue = target.getValue();
            String targetType = target.getTargetType().getSimpleName();

            message = String.format("'%s' 필드의 타입이 올바르지 않습니다. (기대 타입: %s)", fieldName, targetType);
            details.put("field", fieldName);
            details.put("rejectedValue", rejectedValue);
            details.put("expectedType", targetType);
        } else if (cause instanceof JsonParseException) {
            message = "JSON 문법이 올바르지 않습니다. 괄호나 콤마를 확인해주세요.";
        }

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(e, "MALFORMED_JSON", message, status.value(), details));
    }

    // 용량 초과 예외
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e) {
        log.warn("MaxUploadSizeExceededException: {}", e.getMessage());

        String message = "업로드 가능한 최대 용량을 초과했습니다. (개별 파일 최대 10MB, 총합 50MB 이하만 가능)";
        HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
        Map<String, Object> details = Map.of(
                "maxFileSize", "10MB",
                "maxRequestSize", "50MB"
        );

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(e, "PAYLOAD_TOO_LARGE", message, status.value(), details));
    }

    // 그 외 알수 없는 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(e, "INTERNAL_SERVER_ERROR", e.getMessage(), status.value(),
                        null));
    }
}