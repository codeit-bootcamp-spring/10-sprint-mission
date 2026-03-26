package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        String code,
        String message,
        Map<String, Object> details,
        String exceptionType, //발생한 예외의 클래스 이름
        int status // HTTP 상태 코드
) {
    // 커스텀 예외 -> errorResponse
    public static ErrorResponse fromException(DiscodeitException e) {
        return new ErrorResponse(
                e.getTimestamp(),
                e.getErrorCode().getCode(),
                e.getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                e.getErrorCode().getStatus().value()
        );
    }
    // 나머지 일반 예외 -> 500 에러
    public static ErrorResponse fromInternalServerError(Exception e) {
        return new ErrorResponse(
                Instant.now(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                Map.of(),
                e.getClass().getSimpleName(),
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value()
        );
    }
}