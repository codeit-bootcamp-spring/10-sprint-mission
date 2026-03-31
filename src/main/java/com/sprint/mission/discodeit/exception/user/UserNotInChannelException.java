package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserNotInChannelException extends UserException {

  public UserNotInChannelException(UUID userId) {
    super(ErrorCode.USER_NOT_IN_CHANNEL, Map.of("userId", userId));
  }

}
