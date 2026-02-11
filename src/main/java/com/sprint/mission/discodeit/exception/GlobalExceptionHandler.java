package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoNoSuchElementException(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse("NO_SUCH_ELEMENT", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NO_CONTENT);
    }

}
