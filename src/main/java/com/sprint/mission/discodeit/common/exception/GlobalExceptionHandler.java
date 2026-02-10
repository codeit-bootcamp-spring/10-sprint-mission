package com.sprint.mission.discodeit.common.exception;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class,
            NoSuchElementException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        String message = e.getMessage();
        if (e instanceof MethodArgumentTypeMismatchException) {
            message = "입력값의 형식이 올바르지 않습니다.";
        } else if (e instanceof NoSuchElementException) {
            message = "해당 ID로 데이터를 찾을 수 없습니다.";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message, 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(("서버 오류 발생"), 500));
    }

}
