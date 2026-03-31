package com.sprint.mission.discodeit.exception;

import java.util.NoSuchElementException;

import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFounException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  // 도메인 별 예외 처리
  // UserNotFoundException
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e){
    e.printStackTrace();
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                    e.getTimestamp(),
                    e.getErrorCode().toString(),
                    e.getErrorCode().getMessage(),
                    e.getDetails(),
                    "UserNotFoundException",
                    404));
  }

  // ChannelNotFoundException
  @ExceptionHandler(ChannelNotFounException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFoundException(ChannelNotFounException e) {
    e.printStackTrace();
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                    e.getTimestamp(),
                    e.getErrorCode().toString(),
                    e.getErrorCode().getMessage(),
                    e.getDetails(),
                    "ChannelNotFoundException",
                    404
            ));
  }

  // UserAlreadyExistException
  @ExceptionHandler(UserAlreadyExistException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException e) {
    e.printStackTrace();
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(
                    e.getTimestamp(),
                    e.getErrorCode().toString(),
                    e.getErrorCode().getMessage(),
                    e.getDetails(),
                    "UserAlreadyExistException",
                    409
            ));
  }

  //
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    e.printStackTrace();
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(e.getMessage());
  }
}
