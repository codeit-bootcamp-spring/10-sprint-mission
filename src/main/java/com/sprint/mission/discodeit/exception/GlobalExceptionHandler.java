package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            DiscodeitException ex
    ) {
        logException(ex);
        return ResponseEntity.status(ex.getHttpStatus()).body(toErrorResponse(ex));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        InvalidRequestException converted = new InvalidRequestException(
                ex.getMessage(),
                java.util.Map.of(),
                ex
        );
        return handleApiException(converted);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex
    ) {
        UnsupportedMediaTypeDiscodeitException converted =
                new UnsupportedMediaTypeDiscodeitException(
                        ex.getMessage(),
                        java.util.Map.of(),
                        ex
                );
        return handleApiException(converted);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        java.util.Map<String, Object> validationErrors = new java.util.LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors().forEach(error ->
                validationErrors.put(error.getObjectName(), error.getDefaultMessage()));

        InvalidRequestException converted = new InvalidRequestException(
                "요청 데이터 검증에 실패했습니다.",
                java.util.Map.of("validationErrors", validationErrors),
                ex
        );
        return handleApiException(converted);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex
    ) {
        InternalDiscodeitException converted = new InternalDiscodeitException(
                ErrorCode.INTERNAL_ERROR.getDefaultMessage(),
                java.util.Map.of(),
                ex
        );
        return handleApiException(converted);
    }

    private ErrorResponse toErrorResponse(DiscodeitException ex) {
        return new ErrorResponse(
                ex.getTimestamp(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                ex.getDetails(),
                ex.getClass().getSimpleName(),
                ex.getHttpStatus().value()
        );
    }

    private void logException(DiscodeitException ex) {
        if (ex.getHttpStatus().is4xxClientError()) {
            log.warn("{} [{}]", ex.getMessage(), ex.getErrorCode().getCode(), ex);
            return;
        }

        log.error("{} [{}]", ex.getMessage(), ex.getErrorCode().getCode(), ex);
    }
}
