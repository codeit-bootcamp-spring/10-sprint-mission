package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BusinessLogicException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(BusinessLogicException e) {
    log.error("BusinessLogicException", e);

    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse body = ErrorResponse.from(errorCode, e.getMessage());
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
    log.error("BadRequest Exception", e);

    ErrorCode code =
        (e instanceof MethodArgumentTypeMismatchException) ? ErrorCode.TYPE_MISMATCH
            : (e instanceof HttpMessageNotReadableException) ? ErrorCode.INVALID_JSON
                : ErrorCode.BAD_REQUEST;

    ErrorResponse body = ErrorResponse.from(code, e.getMessage());
    return ResponseEntity.status(code.getHttpStatus()).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception e) {
    log.error("Unhandled Exception", e);

    ErrorCode code = ErrorCode.INTERNAL_ERROR;
    ErrorResponse body = ErrorResponse.from(code, null);
    return ResponseEntity.status(code.getHttpStatus()).body(body);
  }
}