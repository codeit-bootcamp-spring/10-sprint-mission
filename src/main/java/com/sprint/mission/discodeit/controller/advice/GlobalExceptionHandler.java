package com.sprint.mission.discodeit.controller.advice;

import com.sprint.mission.discodeit.controller.dto.ErrorResponseDTO;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponseDTO> handleDiscodeitException(
            DiscodeitException e
    ) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(new ErrorResponseDTO(
                        e.getTimestamp(),
                        e.getErrorCode().getStatus(),
                        e.getErrorCode().getErrorType(),
                        e.getErrorCode().getMessage(),
                        e.getDetails(),
                        e.getClass().getSimpleName()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentValidException(
            MethodArgumentNotValidException e
    ) {
        Map<String, Object> details = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponseDTO(
                        Instant.now(),
                        400,
                        "VALIDATION_ERROR",
                        "요청 값이 올바르지 않습니다.",
                        details,
                        e.getClass().getSimpleName()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponseDTO(
                        Instant.now(),
                        500,
                        "INTERNAL_SERVER_ERROR",
                        "서버 내부 오류가 발생했습니다.",
                        Map.of(),
                        "INTERNAL_SERVER_ERROR"
                ));
    }
}
