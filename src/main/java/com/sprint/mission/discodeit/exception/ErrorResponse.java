package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ErrorResponse {

    private final Instant timestamp;
    private final String code;
    private final String message;
    private final java.util.Map<String, Object> details;
    private final String exceptionType;
    private final int status;

    // DiscodeitException
    public static ErrorResponse of(DiscodeitException ex) {
        Map<String, Object> responseDetails = ex.getErrorCode().getStatus().is5xxServerError()
                ? Map.of("error", "서버 내부 오류 발생")
                : ex.getDetails();

        return new ErrorResponse(
                ex.getTimestamp(),
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getMessage(),
                responseDetails,
                ex.getClass().getSimpleName(),
                ex.getErrorCode().getStatusValue()
        );
    }

    // 그 외 Exception
    public static ErrorResponse of(Exception e, String code, String message, int status,
            Map<String, Object> details) {
        return new ErrorResponse(
                Instant.now(),
                code,
                message,
                details == null ? Map.of("error", "서버 내부 오류 발생") : details,
                e.getClass().getSimpleName(),
                status
        );
    }
}
