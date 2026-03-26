package com.sprint.mission.discodeit.exception;

import java.util.NoSuchElementException;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleException(IllegalArgumentException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleException(NoSuchElementException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(UserAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExist(UserAlreadyExistException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFound(ChannelNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(PrivateChannelUpdateException.class)
  public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateException(PrivateChannelUpdateException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(EmailAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExistException(EmailAlreadyExistException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(UserNameAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleUserNameAlreadyExistException(UserNameAlreadyExistException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotFound(MessageNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }
}
