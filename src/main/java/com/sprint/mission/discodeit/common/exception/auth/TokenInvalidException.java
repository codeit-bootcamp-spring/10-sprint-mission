package com.sprint.mission.discodeit.common.exception.auth;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;

public class TokenInvalidException extends DiscodeitException {

  public TokenInvalidException() {
    super(ErrorCode.INVALID_TOKEN, Map.of());
  }
}