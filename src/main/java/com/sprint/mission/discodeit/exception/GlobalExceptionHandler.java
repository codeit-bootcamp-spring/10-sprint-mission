package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 BAD REQUEST
    @ExceptionHandler({
            IllegalArgumentException.class,
            MessageNotInputException.class,
            ZeroLengthPasswordException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception e) {
        return error(HttpStatus.BAD_REQUEST,
                e instanceof MethodArgumentTypeMismatchException
                        ? "요청 파라미터 타입이 올바르지 않습니다."
                        : e.getMessage()
        );
    }

    // 401 UNAUTHORIZED
    @ExceptionHandler(CanNotLoginException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(Exception e) {
        return error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // 404 NOT FOUND
    @ExceptionHandler({
            UserNotFoundException.class,
            ChannelNotFoundException.class,
            MessageNotFoundException.class,
            StatusNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(Exception e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 409 CONFLICT
    @ExceptionHandler({
            DuplicationUserException.class,
            DuplicationEmailException.class,
            DuplicationChannelException.class,
            DuplicationReadStatusException.class,
            AlreadyJoinedChannelException.class,
            UserNotInChannelException.class,
            IllegalStateException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(Exception e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    // 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleServerError(Exception e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");
    }

    // 공통 응답 포맷
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                Map.of(
                        "timestamp", Instant.now(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                )
        );
    }
}
