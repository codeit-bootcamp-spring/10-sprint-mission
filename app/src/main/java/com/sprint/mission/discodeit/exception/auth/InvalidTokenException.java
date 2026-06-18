package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidTokenException extends AuthException {

  public InvalidTokenException() {
    super(ErrorCode.INVALID_TOKEN);
  }
}
