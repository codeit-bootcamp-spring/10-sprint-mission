package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        ErrorCode errorCode = ErrorCode.resolve(e);

        String messageOverride = normalizeMessage(e);
        ErrorResponse body = ErrorResponse.from(errorCode, messageOverride);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }

    private String normalizeMessage(Exception e) {
        if (e instanceof MethodArgumentTypeMismatchException) {
            return "요청 파라미터 타입이 올바르지 않습니다.";
        }
        if (e instanceof HttpMessageNotReadableException) {
            return "요청 바디(JSON)가 올바르지 않습니다.";
        }
        String msg = e.getMessage();
        return (msg == null || msg.isBlank()) ? null : msg;
    }
}
