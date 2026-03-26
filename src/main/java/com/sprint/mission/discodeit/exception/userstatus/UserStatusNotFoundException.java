package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusNotFoundException extends UserStatusException{

  private static final ErrorCode DEFAULT_CODE = ErrorCode.USER_NOT_FOUND;

  public UserStatusNotFoundException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public UserStatusNotFoundException() {
    super(DEFAULT_CODE);
  }

  public UserStatusNotFoundException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public UserStatusNotFoundException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
