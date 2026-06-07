package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidRefreshTokenException extends UserException {

  public InvalidRefreshTokenException(ErrorCode errorCode) {
    super(errorCode);
  }
}
