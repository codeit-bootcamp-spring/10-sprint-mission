package com.sprint.mission.discodeit.exception;


import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.warn("DiscodeitException: {}", e.getErrorCode(), e);

    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse body = ErrorResponse.from(errorCode, e.getMessage(), e.getClass().getSimpleName(),
        e.getDetails());
    return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
  }

  @ExceptionHandler({
      IllegalArgumentException.class,
      HttpMessageNotReadableException.class,
      MethodArgumentTypeMismatchException.class,
      MissingServletRequestParameterException.class,
      MissingServletRequestPartException.class,
      HttpMediaTypeNotSupportedException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
    log.warn("BadRequest Exception", e);

    ErrorCode code =
        (e instanceof MethodArgumentTypeMismatchException) ? ErrorCode.TYPE_MISMATCH
            : (e instanceof HttpMessageNotReadableException) ? ErrorCode.INVALID_JSON
                : ErrorCode.BAD_REQUEST;

    ErrorResponse body = ErrorResponse.from(code, code.getMessage(), e.getClass().getSimpleName());
    return ResponseEntity.status(code.getHttpStatus()).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception e) {
    log.error("Unhandled Exception", e);

    ErrorCode code = ErrorCode.INTERNAL_ERROR;
    ErrorResponse body = ErrorResponse.from(code, code.getMessage(), e.getClass().getSimpleName());
    return ResponseEntity.status(code.getHttpStatus()).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e
  ) {
    log.warn("Validation Exception", e);

    Map<String, Object> errors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            error -> error.getDefaultMessage() != null
                ? error.getDefaultMessage()
                : "유효하지 않은 값입니다.",
            (first, second) -> first,
            LinkedHashMap::new
        ));

    ErrorResponse response = ErrorResponse.from(
        ErrorCode.VALIDATION_FAILED,
        null,
        e.getClass().getSimpleName(),
        errors
    );

    return ResponseEntity.badRequest().body(response);
  }
}

