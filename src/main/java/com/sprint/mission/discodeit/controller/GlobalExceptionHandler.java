package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}

    // 없는 데이터 조회
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), req.getRequestURI());
    }

    // 유효하지 않은 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req.getRequestURI());
    }

    // JSON 파싱 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBodyParse(HttpMessageNotReadableException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "요청 바디(JSON) 형식이 올바르지 않습니다.", req.getRequestURI());
    }

    // 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "요청 파라미터 형식이 올바르지 않습니다.", req.getRequestURI());
    }

    // 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "요청 값 검증에 실패했습니다.", req.getRequestURI());
    }

    // 나머지 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.", req.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(body, headers, status);
    }
}
