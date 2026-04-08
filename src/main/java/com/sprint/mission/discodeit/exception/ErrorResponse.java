package com.sprint.mission.discodeit.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Schema(description = "공통 예외 응답")
public record ErrorResponse(

        @Schema(description = "예외 발생 시각", example = "2026-02-23T05:23:49.657764500Z")
        Instant timestamp,

        @Schema(description = "예외 코드", example = "INVALID_CREDENTIALS")
        String code,

        @Schema(description = "예외 메시지", example = "사용자 이름 또는 비밀번호가 올바르지 않습니다.")
        String message,

        @Schema(description = "예외 상세 정보")
        Map<String, Object> details,

        @Schema(description = "발생한 예외 클래스 이름", example = "InvalidCredentialsException")
        String exceptionType,

        @Schema(description = "HTTP 상태 코드", example = "401")
        int status
) {

    public ErrorResponse {
        timestamp = timestamp == null ? Instant.now() : timestamp;
        details = details == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    public static ErrorResponse of(DiscodeitException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return new ErrorResponse(
                exception.getTimestamp(),
                errorCode.name(),
                exception.getMessage(),
                exception.getDetails(),
                exception.getClass().getSimpleName(),
                errorCode.getHttpStatus().value()
        );
    }

    public static ErrorResponse of(ErrorCode code, String exceptionType, String message,
            Map<String, Object> details) {
        String resolvedMessage = (message == null || message.isBlank())
                ? code.getMessage()
                : message;

        return new ErrorResponse(
                Instant.now(),
                code.name(),
                resolvedMessage,
                details,
                exceptionType,
                code.getHttpStatus().value()
        );
    }

    public static ErrorResponse of(ErrorCode code, String exceptionType, String message) {
        return of(code, exceptionType, message, Collections.emptyMap());
    }

    public static ErrorResponse of(ErrorCode code, String exceptionType) {
        return of(code, exceptionType, null);
    }
}
