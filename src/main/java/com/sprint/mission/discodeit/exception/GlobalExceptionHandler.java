package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 모든 커스텀 예외 통합 처리
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException ex) {
        HttpStatus status = ex.getErrorCode().getStatus();
        // 5XX 일 경우 error로 로그 남김
        if (status.is5xxServerError()){
            log.error("[{}] {} - details: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex.getDetails());
        }else{
            log.warn("[{}] {} - details: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex.getDetails());
        }
        return ResponseEntity.status(status).body(ErrorResponse.from(ex));
    }

    // Dto - Valid 검증 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        /*
        MethodArgumentNotValidException에는 어떤 필드가 왜 실패했는지 정보가 담겨있음
        BindingResult {
            FieldError { field: "username", message: "must not be blank" }
            FieldError { field: "password", message: "must not be blank" }
        } <- 이런 식으로
        만약 Exception 자체를 그대로 넘기게 된다면 ErrorResponse로 매핑이 안된다!!
        이때, bindingResult의 message는 한 개 이상일수도 있다 ! 그래서 람다로 처리하는 게 좋을 듯
         */
        Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                error -> error.getDefaultMessage() != null
                                        ? error.getDefaultMessage()
                                        : "유효하지 않은 값입니다"
                        ));

        log.warn("[MethodArgumentNotValidException] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(HttpStatus.BAD_REQUEST, details, ex));
    }

    // 메서드 레벨 권한 없음 핸들러 메서드 추가
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("[AccessDeniedException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.from(HttpStatus.FORBIDDEN, ex));
    }

    // 클라이언트 요청 오류
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[IllegalArgumentException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.from(HttpStatus.BAD_REQUEST, ex));
    }

    // 처리 불가 오류
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("[IllegalStateException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.from(HttpStatus.CONFLICT, ex));
    }

    // Content-Type 불일치
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        log.warn("[HttpMediaTypeNotSupportedException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.from(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex));
    }

    // 서버 내부 문제
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("[Exception] 예상치 못한 오류 발생 ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.from(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }
}