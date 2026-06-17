package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNotFoundException extends UserException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.USER_NOT_FOUND;

  public UserNotFoundException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public UserNotFoundException() {
    super(DEFAULT_CODE);
  }

  public UserNotFoundException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public UserNotFoundException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
