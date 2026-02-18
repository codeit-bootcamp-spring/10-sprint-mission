package com.sprint.mission.discodeit.controller.advice;

import com.sprint.mission.discodeit.controller.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgument(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponseDTO(
                                Instant.now(),
                                400,
                                "잘못된 요청입니다.",
                                e.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDTO(
                        Instant.now(),
                        400,
                        "잘못된 요청입니다.",
                        e.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNoSuchElement(
            NoSuchElementException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponseDTO(
                                Instant.now(),
                                404,
                                "데이터가 존재하지 않습니다.",
                                e.getMessage(),
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(
            Exception e,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponseDTO(
                                Instant.now(),
                                500,
                                "내부 서버 오류입니다.",
                                e.getMessage(),
                                request.getRequestURI()
                        )
                );
    }
}
