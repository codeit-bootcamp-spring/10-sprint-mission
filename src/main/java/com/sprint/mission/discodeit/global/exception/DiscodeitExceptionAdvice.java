package com.sprint.mission.discodeit.global.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DiscodeitExceptionAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleIncorrectRequestArgument(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    ApiErrorResponse apiErrorResponse = ApiErrorResponse.from(exception);
    log.warn(createLogMessage(request, apiErrorResponse));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ApiErrorResponse> handleDiscodeitException(
      DiscodeitException exception, HttpServletRequest request) {
    ApiErrorResponse apiErrorResponse = ApiErrorResponse.from(exception);
    log.warn(createLogMessage(request, apiErrorResponse));
    return ResponseEntity.status(exception.getStatus()).body(apiErrorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleException(
      Exception exception, HttpServletRequest request) {
    ApiErrorResponse apiErrorResponse = ApiErrorResponse.internalServerError();
    String logMessage =
        createLogMessage(request, apiErrorResponse) + " | Cause: " + exception.getMessage();
    log.error(logMessage, exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
  }

  private String createLogMessage(HttpServletRequest request, ApiErrorResponse response) {
    return String.format("[%s %s] Status: %d | Type: %s | Code: %s | Message: %s",
        request.getMethod(),
        request.getRequestURI(),
        response.getStatus().value(),
        response.getExceptionType(),
        response.getCode(),
        response.getMessage()
    );
  }
}
