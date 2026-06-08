package com.sprint.mission.discodeit.channel.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.UUID;

public class ChannelException extends DiscodeitException {

  public ChannelException(ErrorCode errorCode, UUID id) {
    super(errorCode, id);
  }
}
