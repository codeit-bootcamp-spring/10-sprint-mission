package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ErrorResponse {

  private List<FieldError> fieldErrors;
  private List<ConstraintViolationError> violationErrors;
  private Integer statusCode;
  private String message;

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

    private String field;
    private Object rejectedValue;
    private String message;

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

    private String propertyPath;
    private Object rejectedValue;
    private String reason;

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
