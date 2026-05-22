package com.sprint.mission.discodeit.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

  private Instant timestamp;
  private String code;
  private String message;
  private Map<String, Object> details;
  private String exceptionType;//예외 클래스 이름
  private int status;


  public static ErrorResponse of(Exception e) {
    return new ErrorResponse(
        Instant.now(),
        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
        e.getMessage(),
        new HashMap<>(),
        e.getClass().getSimpleName(),
        500
    );
  }

  public static ErrorResponse of(MethodArgumentNotValidException e) {
    Map<String, Object> details = new HashMap<>();
    details.put("errors", FieldError.of(e.getBindingResult()));
    return new ErrorResponse(
        Instant.now(),
        ErrorCode.BAD_REQUEST.getCode(),
        ErrorCode.BAD_REQUEST.getMessage(),
        details,
        e.getClass().getSimpleName(),
        400
    );
  }

  public static ErrorResponse of(ConstraintViolationException e) {
    Map<String, Object> details = new HashMap<>();
    details.put("errors", ConstraintViolationError.of(e.getConstraintViolations()));
    return new ErrorResponse(
        Instant.now(),
        ErrorCode.BAD_REQUEST.getCode(),
        ErrorCode.BAD_REQUEST.getMessage(),
        details,
        e.getClass().getSimpleName(),
        400
    );
  }

  public static ErrorResponse of(DiscodeitException e) {
    return new ErrorResponse(
        e.getTimestamp(),
        e.getErrorCode().getCode(),
        e.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        e.getErrorCode().getStatus()
    );
  }

  public static ErrorResponse of(AuthenticationException e) {

    ErrorCode errorCode;

    if (e instanceof BadCredentialsException) {
      // 로그인 실패
      errorCode = ErrorCode.INVALID_CREDENTIALS; // AU001
    } else if (e instanceof InsufficientAuthenticationException) {
      // auth/me 등 로그인 안한 상황
      errorCode = ErrorCode.UNAUTHORIZED; // AU002
    } else {
      // 3. 그 외
      errorCode = ErrorCode.AUTHENTICATION_FAILED;
    }

    return new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        new HashMap<>(),
        e.getClass().getSimpleName(),
        errorCode.getStatus()
    );
  }

  public static ErrorResponse of(AccessDeniedException e) {
    return new ErrorResponse(
        Instant.now(),
        ErrorCode.ACCESS_DENIED.getCode(),
        ErrorCode.ACCESS_DENIED.getMessage(),
        new HashMap<>(),
        e.getClass().getSimpleName(),
        ErrorCode.ACCESS_DENIED.getStatus()
    );
  }
  //내부 dto
  @Getter
  public static class FieldError {

    private String field;
    private Object rejectedValue;
    private String message;

    private FieldError(String field, Object rejectedValue, String message) {
      this.field = field;
      this.rejectedValue = rejectedValue;
      this.message = message;
    }

    private static List<FieldError> of(BindingResult bindingResult) {
      return bindingResult.getFieldErrors().stream()
          .map(e -> new FieldError(
              e.getField(),
              e.getRejectedValue() == null ? null : e.getRejectedValue(),
              e.getDefaultMessage()
          )).collect(Collectors.toList());
    }
  }

  @Getter
  public static class ConstraintViolationError {

    private String propertyPath;
    private Object rejectedValue;
    private String message;

    private ConstraintViolationError(String propertyPath, Object rejectedValue, String message) {
      this.propertyPath = propertyPath;
      this.rejectedValue = rejectedValue;
      this.message = message;
    }

    private static List<ConstraintViolationError> of(Set<ConstraintViolation<?>> violations) {
      return violations.stream()
          .map(v -> new ConstraintViolationError(
              v.getPropertyPath().toString(),
              v.getInvalidValue(),
              v.getMessage()
          )).collect(Collectors.toList());
    }
  }
}
