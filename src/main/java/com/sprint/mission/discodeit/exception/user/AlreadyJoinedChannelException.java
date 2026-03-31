package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class AlreadyJoinedChannelException extends UserException {

  public AlreadyJoinedChannelException(UUID userId) {
    super(ErrorCode.ALREADY_JOINED_CHANNEL, Map.of("userId", userId));
  }

}
