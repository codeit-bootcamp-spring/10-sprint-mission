package com.sprint.mission.discodeit.common.exception.user;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistException extends UserException {

  public UserAlreadyExistException(Map<String, Object> details) {
    super(ErrorCode.USER_ALREADY_EXIST, details);
  }

}
