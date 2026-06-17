package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleException(DiscodeitException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = getHttpStatus(errorCode);

        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("[Exception] 커스텀 예외: timestamp={}, code={}, message={}, details={}",
                    e.getTimestamp(), e.getErrorCode().name(), e.getMessage(), e.getDetails(), e);
        } else {
            log.warn("[Exception] 커스텀 예외: timestamp={}, code={}, message={}, details={}",
                    e.getTimestamp(), e.getErrorCode().name(), e.getMessage(), e.getDetails(), e);
        }

        ErrorResponse errorResponse = new ErrorResponse(e, status.value());

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[Exception] 예상하지 못한 예외: code={}, message={}",
                e.getClass().getSimpleName(), e.getMessage(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(e, status.value());

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        log.warn("[EXCEPTION] Bean Validation 예외: code={}, message={}",
                e.getClass().getSimpleName(), e.getMessage(), e);

        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fe -> details.put(fe.getField(), fe.getDefaultMessage()));

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "BEAN_VALIDATION",
                "유효하지 않은 값입니다.",
                details,
                e.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("[AUTH_ACCESS_DENIED] 권한이 부족한 요청: code={}, message={}",
                e.getClass().getSimpleName(), e.getMessage(), e);

        int status = HttpStatus.FORBIDDEN.value();
        ErrorResponse errorResponse = ErrorResponse.accessDenied(e, status);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException e) {
        log.warn("[EXCEPTION] 적합하지 않은 HTTP Request Body: code={}, message={}",
                e.getClass().getSimpleName(), e.getMessage(), e);

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "INVALID_HTTP_REQUEST_BODY",
                "적합하지 않은 HTTP Request Body입니다.",
                new HashMap<>(),
                e.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentTypeMismatchException e) {
        log.warn("[EXCEPTION] 요청 파라미터 타입 형식 예외: code={}, message={}",
                e.getClass().getSimpleName(), e.getMessage(), e);

        Map<String, Object> details = new HashMap<>();
        details.put(e.getName(), e.getValue()); // (파라미터 필드 이름, 파라미터 필드 값)

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "INVALID_PARAMETER_TYPE",
                "요청 파라미터 형식이 올바르지 않습니다.",
                details,
                e.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case INVALID_INPUT, NO_CHANGE_VALUE, LOGIN_FAILED,
                 PROFILE_UPLOAD_FAILED, ATTACHMENTS_UPLOAD_FAILED,
                 PRIVATE_CHANNEL_PARTICIPANT_REQUIRED, PRIVATE_CHANNEL_CANNOT_BE_UPDATED,
                 DUPLICATED_USERNAME, DUPLICATED_EMAIL,
                 DUPLICATED_READ_STATUS -> HttpStatus.BAD_REQUEST;
            case INVALID_JWT_TOKEN, INVALID_ACCESS_TOKEN, INVALID_REFRESH_TOKEN,
                 INVALID_JWT_INFORMATION -> HttpStatus.UNAUTHORIZED;
            case USER_NOT_FOUND, READ_STATUS_NOT_FOUND,
                 MESSAGE_NOT_FOUND, CHANNEL_NOT_FOUND, BINARY_CONTENT_NOT_FOUND,
                 PROFILE_NOT_FOUND, NOTIFICATION_NOT_FOUNT, JWT_INFORMATION_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case PROFILE_READ_FAILED, BINARY_CONTENT_SAVE_FAILED, BINARY_CONTENT_READ_FAILED,
                 BINARY_CONTENT_STORAGE_INIT_FAILED, PRESIGNED_URL_CREATE_FAILED,
                 INTERNAL_SERVER_ERROR, AWS_SERVER_CONNECT_FAILED, JWT_TOKEN_CREATE_FAILED,
                 EVENT_SERIALIZATION_FAILED, EVENT_DESERIALIZATION_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
