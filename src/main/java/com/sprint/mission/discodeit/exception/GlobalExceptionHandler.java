package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException e,  HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        return buildResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage(), request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return buildResponse(errorCode.getStatus(), errorCode.getCode(), e.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return buildResponse(errorCode.getStatus(), errorCode.getCode(), e.getMessage(), request);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(HttpStatus status, String code, String message, HttpServletRequest request) {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(response, status);
    }
}