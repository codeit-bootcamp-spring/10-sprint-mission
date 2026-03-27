package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(ErrorResponse.of(e));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
//        return ResponseEntity
//            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//            .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNoSuchElement(NoSuchElementException e) {
//        log.warn(e.getMessage());
//        return ErrorResponse.of(404, e.getMessage());
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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

//    @ExceptionHandler({
//        NullPointerException.class,
//        IllegalArgumentException.class,
//        DataIntegrityViolationException.class
//    })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleBadRequest(Exception e) {
//        log.warn(e.getMessage());
//        return ErrorResponse.of(400, e.getMessage());
//    }

//    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
//    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
//    public ErrorResponse handlePayloadExceed(Exception e) {
//        log.warn(e.getMessage());
//        return ErrorResponse.of(413, "파일 용량이 너무 큽니다");
//    }

}
