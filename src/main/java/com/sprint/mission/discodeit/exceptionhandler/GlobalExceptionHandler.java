package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e,
        HttpServletRequest req) {
        log.warn(e.getMessage());

        log.warn("api={} {} message={}", req.getMethod(), req.getRequestURI(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(ErrorResponse.of(e));

    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
//        return ResponseEntity
//            .status(HttpStatus.INTERNAL_SERVER_ERROR)
//            .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
//    }

//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleIllegalStateMethod(IllegalStateException e) {
//        log.warn(e.getMessage());
//        return ErrorResponse.of(400, e.getMessage());
//    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNoSuchElement(NoSuchElementException e) {
//        log.warn(e.getMessage());
//        return ErrorResponse.of(404, e.getMessage());
//    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
//        String message = e.getBindingResult()
//            .getFieldErrors()
//            .stream()
//            .map(fieldError -> fieldError.getDefaultMessage())
//            .filter(Objects::nonNull)
//            .findFirst()
//            .orElse("요청 값이 올바르지 않습니다.");
//        log.warn(e.getMessage());
//        return ErrorResponse.of(400, message);
//    }

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
