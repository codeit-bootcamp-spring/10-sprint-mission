package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserAlreadyExistsException extends UserException {

  public UserAlreadyExistsException(UUID userId) {
    super(ErrorCode.DUPLICATION_USER, Map.of("userId", userId));

  }

  public UserAlreadyExistsException(String userName) {
    super(ErrorCode.DUPLICATION_USER, Map.of("userName", userName));

  }

}
