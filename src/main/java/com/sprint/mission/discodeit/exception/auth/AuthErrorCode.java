package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
  USERNAME_OR_PASSWORD_INCORRECT(404, "U002", "Username or password incorrect"),
  ;

  private final int status;
  private final String code;
  private final String message;
}
