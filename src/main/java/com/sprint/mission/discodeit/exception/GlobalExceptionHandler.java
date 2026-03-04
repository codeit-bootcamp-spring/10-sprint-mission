package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler
//    public ResponseEntity<ErrorResponse> handleException(Exception e) {
//        e.printStackTrace();
//        ErrorResponse errorResponse = ErrorResponse.of(500, "Internal Server Error", "예상치 못한 오류입니다");
//        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
//    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(
                400, "VALIDATION_ERROR", "요청 값이 올바르지 않습니다.", e.getBindingResult());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.of(
                400, "VALIDATION_ERROR", "요청 값이 올바르지 않습니다.", e.getConstraintViolations());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException e) {
        ErrorResponse errorResponse = ErrorResponse.of(e.getStatus(), e.getCode(), e.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
