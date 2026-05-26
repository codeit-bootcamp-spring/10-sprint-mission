package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class TokenParseException extends AuthException {

  public TokenParseException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.TOKEN_PARSE_ERROR, details, cause);
  }
}
