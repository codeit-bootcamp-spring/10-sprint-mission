package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolation;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;

@Getter
public class ErrorResponse {

  private final Instant timestamp;
  private final String code;
  private final String message;
  private final Map<String, Object> details;
  private final String exceptionType;
  private final int status;

  private ErrorResponse(Instant timestamp, String code, String message,
      Map<String, Object> details,
      String exceptionType, int status) {
    this.timestamp = timestamp;
    this.code = code;
    this.message = message;
    this.details = details;
    this.exceptionType = exceptionType;
    this.status = status;
  }

  public static ErrorResponse of(int status, String exceptionType,
      BindingResult bindingResult) {
    List<ErrorDetail> details = ErrorDetail.of(bindingResult);
    String message = details.isEmpty() ? null : details.get(0).getReason();

    Map<String, Object> mapDetails = new HashMap<>();
    for (ErrorDetail d : details) {
      mapDetails.put(d.getName(), d.getRejectedValue());
    }

    return new ErrorResponse(Instant.now(), "VALIDATION_ERROR", message,
        mapDetails, exceptionType, status);
  }

  public static ErrorResponse of(int status, String exceptionType,
      Set<ConstraintViolation<?>> constraintViolations) {
    List<ErrorDetail> details = ErrorDetail.of(constraintViolations);
    String message = details.isEmpty() ? null : details.get(0).getReason();

    Map<String, Object> mapDetails = new HashMap<>();
    for (ErrorDetail d : details) {
      mapDetails.put(d.getName(), d.getRejectedValue());
    }

    return new ErrorResponse(Instant.now(), "VALIDATION_ERROR", message,
        mapDetails, exceptionType, status);
  }

  public static ErrorResponse of(int status, Exception exception) {
    return new ErrorResponse(Instant.now(), "INTERNAL_ERROR", "예기치 못한 오류가 발생했습니다",
        null, "Exception", status);
  }

  public static ErrorResponse of(int status, AuthorizationDeniedException exception) {
    return new ErrorResponse(
        Instant.now(), "AUTHORIZED_DENIED", "권한이 없습니다", null,
        exception.getClass().getSimpleName(), status
    );
  }

  public static ErrorResponse of(int status, DiscodeitException exception) {
    return new ErrorResponse(
        exception.getTimestamp(),
        exception.getErrorCode().getCode(),
        exception.getMessage(),
        exception.getDetails(),
        exception.getClass().getSimpleName(),
        status
    );
  }

  @Getter
  public static class ErrorDetail {

    private final String name;
    private final Object rejectedValue;
    private final String reason;

    private ErrorDetail(String name, Object rejectedValue, String reason) {
      this.name = name;
      this.rejectedValue = rejectedValue;
      this.reason = reason;
    }

    public static List<ErrorDetail> of(BindingResult bindingResult) {
      return bindingResult.getFieldErrors().stream()
          .map(err -> new ErrorDetail(
              err.getField(),
              err.getRejectedValue(),
              err.getDefaultMessage()
          )).collect(Collectors.toList());
    }

    public static List<ErrorDetail> of(Set<ConstraintViolation<?>> constraintViolations) {
      return constraintViolations.stream()
          .map(cv -> new ErrorDetail(
              cv.getPropertyPath().toString(),
              cv.getInvalidValue(),
              cv.getMessage()
          )).collect(Collectors.toList());
    }
  }
}