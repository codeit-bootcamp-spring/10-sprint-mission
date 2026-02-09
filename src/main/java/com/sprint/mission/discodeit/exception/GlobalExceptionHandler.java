package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.user.exception.EmailDuplicationException;
import com.sprint.mission.discodeit.user.exception.AuthenticationFailedException;
import com.sprint.mission.discodeit.user.exception.UserDuplicationException;
import com.sprint.mission.discodeit.user.exception.UserNotFoundException;
import com.sprint.mission.discodeit.userstatus.exception.UserStatusDuplicationException;
import com.sprint.mission.discodeit.userstatus.exception.UserStatusNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailDuplicationException.class)
    public ResponseEntity<ErrorResponse> handle(EmailDuplicationException e) {
        ErrorResponse response = new ErrorResponse("EMAIL_DUPLICATION_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(UserNotFoundException e) {
        ErrorResponse response = new ErrorResponse("USER_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserDuplicationException.class)
    public ResponseEntity<ErrorResponse> handle(UserDuplicationException e) {
        ErrorResponse response = new ErrorResponse("USER_DUPLICATION_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserStatusNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(UserStatusNotFoundException e) {
        ErrorResponse response = new ErrorResponse("USER_STATUS_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserStatusDuplicationException.class)
    public ResponseEntity<ErrorResponse> handle(UserStatusDuplicationException e) {
        ErrorResponse response = new ErrorResponse("USER_STATUS_DUPLICATION_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handle(AuthenticationFailedException e) {
        ErrorResponse response = new ErrorResponse("AUTHENTICATION_FAILED", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException e) {
        ErrorResponse response = new ErrorResponse("INVALID_PARAMETER_TYPE", "해당 파라미터가 유효하지 않습니다.");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessException e) {
        ErrorResponse response = new ErrorResponse("UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다.");
        return ResponseEntity.badRequest().body(response);
    }
}
