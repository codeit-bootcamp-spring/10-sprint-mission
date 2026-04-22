package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusAlreadyExistException extends UserStatusException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.USER_STATUS_ALREADY_EXISTS;

  public UserStatusAlreadyExistException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public UserStatusAlreadyExistException() {
    super(DEFAULT_CODE);
  }

  public UserStatusAlreadyExistException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public UserStatusAlreadyExistException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
