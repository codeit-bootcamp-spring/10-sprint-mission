package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class AccessDeniedSecurityException extends DiscodeitException {

  private AccessDeniedSecurityException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.ACCESS_DENIED, details, cause);
  }

  public static AccessDeniedSecurityException withPath(String path, Throwable cause) {
    return new AccessDeniedSecurityException(Map.of("path", path), cause);
  }
}