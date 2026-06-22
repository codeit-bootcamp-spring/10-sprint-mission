package com.sprint.mission.discodeit.global.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class DiscodeitExceptionAdvice extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    ApiErrorResponse apiErrorResponse = ApiErrorResponse.from(ex);
    log.warn(createLogMessage(request, apiErrorResponse));
    return ResponseEntity.status(status).headers(headers).body(apiErrorResponse);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode,
      WebRequest request) {

    ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
        .status(HttpStatus.resolve(statusCode.value()))
        .message(ex.getMessage())
        .build();
    log.warn("{} | Spring MVC Exception: {}", createLogMessage(request, apiErrorResponse),
        ex.getMessage());
    return ResponseEntity.status(statusCode).headers(headers).body(apiErrorResponse);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ApiErrorResponse> handleDiscodeitException(
      DiscodeitException exception, HttpServletRequest request) {

    ApiErrorResponse apiErrorResponse = ApiErrorResponse.from(exception);
    log.warn(createLogMessage(request, apiErrorResponse));
    return ResponseEntity.status(exception.getStatus()).body(apiErrorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGlobalException(
      Exception exception, HttpServletRequest request) {

    ApiErrorResponse apiErrorResponse = ApiErrorResponse.internalServerError();
    String logMessage =
        createLogMessage(request, apiErrorResponse) + " | Cause: " + exception.getMessage();
    log.error(logMessage, exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
  }

  private String createLogMessage(HttpServletRequest request, ApiErrorResponse response) {
    return String.format("[%s %s] Status: %d | Type: %s | Code: %s | Message: %s",
        request.getMethod(), request.getRequestURI(),
        response.getStatus().value(), response.getExceptionType(),
        response.getCode(), response.getMessage()
    );
  }

  private String createLogMessage(WebRequest request, ApiErrorResponse response) {
    HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
    return createLogMessage(httpServletRequest, response);
  }
}
