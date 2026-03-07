package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static ErrorResponse handleIllegalStateMethod(IllegalStateException e) {
        return ErrorResponse.of(400, e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static ErrorResponse handleNoSuchElement(NoSuchElementException e) {
        return ErrorResponse.of(404, e.getMessage());
    }
}
