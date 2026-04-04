package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;

import java.util.Map;
import java.util.NoSuchElementException;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 커스텀 예외 처리 핸들러 메서드
    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(ErrorResponse.of(e));
    }


    // NoSuchElementException 처리 핸들러
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        log.warn("NoSuchElementException 예외 발행", e);
        return ResponseEntity.status(ErrorCode.NO_SUCH_ELEMENT.getStatus())
            .body(ErrorResponse.of(e, ErrorCode.NO_SUCH_ELEMENT, Map.of()));
    }

    // IllegalStateException 처리 핸들러
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        log.warn("IllegalStateException 예외 발행", e);
        return ResponseEntity.status(ErrorCode.ILLEGAL_STATE.getStatus())
            .body(ErrorResponse.of(e, ErrorCode.ILLEGAL_STATE, Map.of()));
    }

    // @Valid 위반 시 발생되는 MethodArgumentNotValidException 처리 핸들
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, Object> details = e.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                fe -> fe.getDefaultMessage() == null ? "유효하지 않은 값입니다." : fe.getDefaultMessage(),
                (a, b) -> a
            ));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(e, ErrorCode.FIELD_NOT_VALID, details));
    }

}
