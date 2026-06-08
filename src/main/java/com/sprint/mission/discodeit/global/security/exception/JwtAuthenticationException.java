package com.sprint.mission.discodeit.global.security.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class JwtAuthenticationException extends DiscodeitException {

  public JwtAuthenticationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
