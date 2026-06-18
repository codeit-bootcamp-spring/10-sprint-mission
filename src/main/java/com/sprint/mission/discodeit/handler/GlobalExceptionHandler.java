package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e){
        log.warn(" [Domain Error] Code: {}, Message: {}, Details: {}",
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getDetails());
        ErrorResponse response = new ErrorResponse(
                e.getTimestamp(),
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                e.getErrorCode().getStatus()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.warn("잘못된 형식의 입력입니다.", e);

        Map<String, Object> details = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    String errorField = error.getField();
                    Object errorValue = error.getRejectedValue();
                    if(errorField.toLowerCase().contains("password")){
                        return;
                    }
                    details.put(errorField,errorValue);
                });

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                "INVALID_INPUT_VALUE",
                "잘못된 입력값입니다",
                details,
                e.getClass().getSimpleName(),
                e.getStatusCode().value()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
