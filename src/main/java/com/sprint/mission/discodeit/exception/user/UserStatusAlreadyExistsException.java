package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusAlreadyExistsException extends UserException {

  public UserStatusAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_USER_STATUS);
  }

  public UserStatusAlreadyExistsException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_USER_STATUS, details);
  }

}
