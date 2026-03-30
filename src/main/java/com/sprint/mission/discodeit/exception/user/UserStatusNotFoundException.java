package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusNotFoundException extends UserException {

  public UserStatusNotFoundException(Map<String, Object> details) {
    super(ErrorCode.USER_STATUS_NOT_FOUND, details);
  }
}
