package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UnauthorizedException extends AuthException {

  public UnauthorizedException() {
    super(ErrorCode.UNAUTHORIZED);
  }

}
