package com.sprint.mission.discodeit.user.domain.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
  USERID_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User id not found"),
  USERNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "U002", "Username already exists"),
  EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "U003", "Email already exists"),
  USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "U004", "Such a username not found"),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}
