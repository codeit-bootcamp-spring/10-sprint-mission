package com.sprint.mission.discodeit.auth.domain.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class AuthException extends DiscodeitException {

  public AuthException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AuthException(ErrorCode errorCode, Object key, Object value) {
    super(errorCode, key, value);
  }
}
