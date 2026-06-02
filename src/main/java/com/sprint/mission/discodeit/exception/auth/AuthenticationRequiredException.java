package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class AuthenticationRequiredException extends DiscodeitException {

  private AuthenticationRequiredException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.AUTHENTICATION_REQUIRED, details, cause);
  }

  private AuthenticationRequiredException(Map<String, Object> details) {
    super(ErrorCode.AUTHENTICATION_REQUIRED, details);
  }

  public static AuthenticationRequiredException withPath(String path, Throwable cause) {
    return new AuthenticationRequiredException(Map.of("path", path), cause);
  }

  public static AuthenticationRequiredException withDetails(String reason) {
    return new AuthenticationRequiredException(Map.of("reason", reason));
  }
}