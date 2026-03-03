package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.sprint.mission.discodeit.controller")
public class GlobalExceptionHandler {

  // 사용자를 찾을 수 없는 경우
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body((errorResponse));
  }

  // 중복된 사용자 이름
  @ExceptionHandler(DuplicateUsernameException.class)
  public ResponseEntity<ErrorResponse>
  handleDuplicateUsernameException(DuplicateUsernameException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  // 중복된 이메일
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  // 잘못된 인증 정보
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
      InvalidCredentialsException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  // 사용자 상태를 찾을 수 없는 경우
  @ExceptionHandler(UserStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserStatusNotFoundException(
      UserStatusNotFoundException e) {
    ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  // IlleganArgumentException (잘못된 입력)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {

    ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT, e.getMessage());
    return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(errorResponse);
  }

//  // 모든 예외의 최상위 처리 (catch-all)
//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<ErrorResponse> handlerAllExceptions(Exception e) {
//    // 실제 운영 환경에서는 로깅을 해야 함
//    e.printStackTrace();
//    ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
//    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//  }
}