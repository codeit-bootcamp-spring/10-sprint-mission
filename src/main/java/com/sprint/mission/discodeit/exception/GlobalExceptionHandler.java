package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponseDto> handleDiscodeitException(DiscodeitException e,
      HttpServletRequest request) {

    ErrorCode errorCode = e.getErrorCode();

    log.warn("[DiscodeitException] Path={}, Code={}, Message={}, Details={}",
        request.getRequestURI(), errorCode.getCode(), e.getMessage(), e.getDetails());

    return buildResponse(
        errorCode.getStatus(),
        errorCode.getCode(),
        errorCode.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        e.getTimestamp(),
        request
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e,
      HttpServletRequest request) {

    log.warn("[Method Argument Not Valid Exception] Path={}, Message={}",
        request.getRequestURI(), e.getMessage());

    String errorMessage = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    Map<String, Object> details = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error ->
        details.put(error.getField(), error.getRejectedValue()));

    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        errorCode.getCode(),
        errorMessage,
        details,
        e.getClass().getSimpleName(),
        Instant.now(),
        request
    );
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
      AccessDeniedException e,
      HttpServletRequest request) {

    log.warn("[Access Denied] Path={}, Message={}", request.getRequestURI(), e.getMessage());

    return buildResponse(
        HttpStatus.FORBIDDEN,
        "FORBIDDEN",
        "해당 리소스에 접근할 권한이 없습니다.",
        null,
        e.getClass().getSimpleName(),
        Instant.now(),
        request
    );
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e,
      HttpServletRequest request) {

    log.error("[Runtime Exception] Path={}, Message={}",
        request.getRequestURI(), e.getMessage(), e);

    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "RUNTIME_ERROR",
        "서버 실행 중 오류가 발생했습니다.",
        null,
        e.getClass().getSimpleName(),
        Instant.now(),
        request
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleException(Exception e,
      HttpServletRequest request) {

    log.error("[System Exception] Path={}, Message={}",
        request.getRequestURI(), e.getMessage(), e);

    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "SYSTEM_ERROR",
        "시스템 내부 오류가 발생했습니다.",
        null,
        e.getClass().getSimpleName(),
        Instant.now(),
        request
    );
  }

  private ResponseEntity<ErrorResponseDto> buildResponse(
      HttpStatus status,
      String code,
      String message,
      Map<String, Object> details,
      String exceptionType,
      Instant timestamp,
      HttpServletRequest request) {

    ErrorResponseDto response = ErrorResponseDto.builder()
        .timestamp(timestamp)
        .status(status.value())
        .code(code)
        .message(message)
        .details(details)
        .exceptionType(exceptionType)
        .path(request.getRequestURI())
        .build();

    return new ResponseEntity<>(response, status);
  }
}
