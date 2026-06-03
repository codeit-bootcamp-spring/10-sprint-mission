package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidTokenException extends AuthException {
  
  public InvalidTokenException(Map<String, Object> details) {
    super(ErrorCode.INVALID_TOKEN, details);
  }
}
