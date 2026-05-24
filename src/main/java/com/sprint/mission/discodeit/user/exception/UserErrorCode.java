package com.sprint.mission.discodeit.user.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
  USERID_NOT_FOUND(404, "U001", "User id not found"),
  USERSTATUS_NOT_FOUND(404, "U002", "UserStatus not found"),
  USERNAME_ALREADY_EXIST(400, "U003", "Username already exists"),
  EMAIL_ALREADY_EXIST(400, "U004", "Email already exists"),
  ;

  private final int status;
  private final String code;
  private final String message;
}
