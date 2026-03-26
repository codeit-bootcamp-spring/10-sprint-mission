package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.NoSuchElementException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateMethod(IllegalStateException e) {
        log.warn(e.getMessage());
        return ErrorResponse.of(400, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElement(NoSuchElementException e) {
        log.warn(e.getMessage());
        return ErrorResponse.of(404, e.getMessage());
    }

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

    @ExceptionHandler({
        NullPointerException.class,
        IllegalArgumentException.class,
        DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(Exception e) {
        log.warn(e.getMessage());
        return ErrorResponse.of(400, e.getMessage());
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handlePayloadExceed(Exception e) {
        log.warn(e.getMessage());
        return ErrorResponse.of(413, "파일 용량이 너무 큽니다");
    }

}
