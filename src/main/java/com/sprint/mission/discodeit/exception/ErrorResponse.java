package com.sprint.mission.discodeit.exception;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;
    private final List<ErrorDetail> errors;

    private ErrorResponse(int status, String code, String message, List<ErrorDetail> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public static ErrorResponse of(int status, String code, String message, BindingResult bindingResult) {
        return new ErrorResponse(status, code, message, ErrorDetail.of(bindingResult));
    }

    public static ErrorResponse of(int status, String code, String message, Set<ConstraintViolation<?>> constraintViolations) {
        return new ErrorResponse(status, code, message, ErrorDetail.of(constraintViolations));
    }

    public static ErrorResponse of(int status, String code, String message) {
        return new ErrorResponse(status, code, message, null);
    }

    @Getter
    public static class ErrorDetail {
        private final ErrorType errorType;
        private final String name;
        private final Object rejectedValue;
        private final String reason;

        private ErrorDetail(ErrorType errorType, String name, Object rejectedValue, String reason) {
            this.errorType = errorType;
            this.name = name;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<ErrorDetail> of(BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(err -> new ErrorDetail(
                            ErrorType.FIELD,
                            err.getField(),
                            err.getRejectedValue(),
                            err.getDefaultMessage()
                    )).collect(Collectors.toList());
        }

        public static List<ErrorDetail> of(Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                    .map(cv -> new ErrorDetail(
                            ErrorType.PARAM,
                            cv.getPropertyPath().toString(),
                            cv.getInvalidValue(),
                            cv.getMessage()
                    )).collect(Collectors.toList());
        }
    }

    private enum ErrorType {
        FIELD,
        PARAM,
    }
}