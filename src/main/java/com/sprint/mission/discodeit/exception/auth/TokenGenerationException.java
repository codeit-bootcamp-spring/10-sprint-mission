package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class TokenGenerationException extends AuthException {

  public TokenGenerationException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.TOKEN_GENERATION_FAILED, details, cause);
  }
}
