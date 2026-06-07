package com.sprint.mission.discodeit.exception.jwt;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class TokenExpiredException extends DiscodeitException {

  public TokenExpiredException() {
    super(ErrorCode.TOKEN_EXPIRED);
  }
}
