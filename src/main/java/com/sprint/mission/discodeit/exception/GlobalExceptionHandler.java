package com.sprint.mission.discodeit.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e,
        HttpServletRequest request) {

        log.warn("[RUN_TIME_EXCEPTION] uri = {}, message = {}", request.getRequestURI(),
            e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NO_SUCH_ELEMENT", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e,
        HttpServletRequest request) {

        log.warn("[ILLEGAL_ARGUMENT_EXCEPTION] uri = {}, message = {}", request.getRequestURI(),
            e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("ILLEGAL_ARGUMENT", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e,
        HttpServletRequest request) {

        log.warn("[EXCEPTION] uri = {}, message = {}", request.getRequestURI(), e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("EXCEPTION", e.getMessage()));
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException e,
        HttpServletRequest request) {

        log.warn("[BUSINESS_LOGIC_EXCEPTION] uri = {}, message = {}", request.getRequestURI(),
            e.getMessage(), e);

        return ResponseEntity.status(e.getStatusCode())
            .body(new ErrorResponse(e.getStatusCode().toString(), e.getMessage()));
    }

}
