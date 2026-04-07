package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PasswordEmptyException extends AuthException {

  public PasswordEmptyException() {
    super(ErrorCode.PASSWORD_EMPTY);
  }

}
