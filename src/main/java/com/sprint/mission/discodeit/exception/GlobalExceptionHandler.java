package com.sprint.mission.discodeit.exception;

import java.util.NoSuchElementException;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentAlreadyExistsException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.storage.FileStorageException;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleException(IllegalArgumentException e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleException(NoSuchElementException e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ErrorResponse error = new ErrorResponse((DiscodeitException) e, status.value());
    return ResponseEntity.status(status).body(error);

  }

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleException(DiscodeitException e) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse((DiscodeitException) e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(UserAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExist(UserAlreadyExistException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFound(ChannelNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(PrivateChannelUpdateException.class)
  public ResponseEntity<ErrorResponse> handlePrivateChannelUpdateException(PrivateChannelUpdateException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(EmailAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExistException(EmailAlreadyExistException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(UserNameAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleUserNameAlreadyExistException(UserNameAlreadyExistException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotFound(MessageNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(BinaryContentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentNotFound(BinaryContentNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(UserStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserStatusNotFound(UserStatusNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(ReadStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusNotFound(ReadStatusNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);

  }

  @ExceptionHandler(BinaryContentAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentAlreadyExists(BinaryContentAlreadyExistsException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(FileStorageException.class)
  public ResponseEntity<ErrorResponse> handleFileStorageException(FileStorageException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    log.warn("[EXCEPTION] {}: {}", e.getClass().getSimpleName(), e.getMessage());
    ErrorResponse error = new ErrorResponse(e, status.value());
    return ResponseEntity.status(status).body(error);
  }
}
