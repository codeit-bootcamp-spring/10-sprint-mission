package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ErrorResponse {

  private final List<FieldError> fieldErrors;
  private final List<ConstraintViolationError> violationErrors;
  private final Integer statusCode;
  private final String message;

  private ErrorResponse(
      List<FieldError> fieldErrors,
      List<ConstraintViolationError> violationErrors
  ) {
    this.fieldErrors = fieldErrors;
    this.violationErrors = violationErrors;
    this.statusCode = null;
    this.message = null;
  }

  private ErrorResponse(Integer statusCode, String message) {
    this.fieldErrors = null;
    this.violationErrors = null;
    this.statusCode = statusCode;
    this.message = message;
  }

  public static ErrorResponse of(BindingResult bindingResult) {
    return new ErrorResponse(FieldError.of(bindingResult), null);
  }

  public static ErrorResponse of(Set<ConstraintViolation<?>> constraintViolations) {
    return new ErrorResponse(null, ConstraintViolationError.of(constraintViolations));
  }

  public static ErrorResponse of(Integer statusCode, String message) {
    return new ErrorResponse(statusCode, message);
  }

  @Getter
  public static class FieldError {

    private final String field;
    private final Object rejectedValue;
    private final String message;

    private FieldError(String field, Object rejectedValue, String message) {
      this.field = field;
      this.rejectedValue = rejectedValue;
      this.message = message;
    }

    public static List<FieldError> of(BindingResult bindingResult) {
      return bindingResult.getFieldErrors().stream()
          .map(error -> new FieldError(
              error.getField(),
              error.getRejectedValue(),
              error.getDefaultMessage()
          )).toList();
    }
  }

  @Getter
  public static class ConstraintViolationError {

    private final String propertyPath;
    private final Object rejectedValue;
    private final String reason;

    private ConstraintViolationError(String propertyPath, Object rejectedValue, String reason) {
      this.propertyPath = propertyPath;
      this.rejectedValue = rejectedValue;
      this.reason = reason;
    }

    public static List<ConstraintViolationError> of(
        Set<ConstraintViolation<?>> constraintViolations) {
      return constraintViolations.stream()
          .map(cv -> new ConstraintViolationError(
              cv.getPropertyPath().toString(),
              cv.getInvalidValue(),
              cv.getMessage()
          )).toList();
    }
  }
}
