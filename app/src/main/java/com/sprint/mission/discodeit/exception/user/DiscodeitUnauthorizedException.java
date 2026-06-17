package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DiscodeitUnauthorizedException extends UserException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.UNAUTHORIZED;

  public DiscodeitUnauthorizedException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public DiscodeitUnauthorizedException() {
    super(DEFAULT_CODE);
  }

  public DiscodeitUnauthorizedException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public DiscodeitUnauthorizedException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
