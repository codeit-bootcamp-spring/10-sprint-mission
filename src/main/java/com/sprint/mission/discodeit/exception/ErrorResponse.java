package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timeStamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType, // 발생한 예외 클래스 이름
        int status  // HTTP 상태코드
) {
    // DiscodeitException -> ErrorResponse
    public static ErrorResponse from(DiscodeitException ex){
        return new ErrorResponse(
                ex.getTimestamp(),
                ex.getErrorCode().name(),
                ex.getMessage(),
                ex.getDetails(),
                ex.getClass().getSimpleName(),
                ex.getErrorCode().getStatus().value()
        );
    }

    // Exception -> ErrorResponse
    public static ErrorResponse from(HttpStatus status, Exception ex){
        return new ErrorResponse(
                Instant.now(),
                status.name(),
                ex.getMessage(),
                Map.of(),
                ex.getClass().getSimpleName(),
                status.value()
        );
    }

    // MethodArgumentNotValidException -> ErrorResponse
    public static ErrorResponse from(HttpStatus status, Map<String, Object> detail, Exception ex){
        return new ErrorResponse(
                Instant.now(),
                status.name(),
                "입력값 검증에 실패하였습니다.",
                detail,
                ex.getClass().getSimpleName(),
                status.value()
        );
    }
}
