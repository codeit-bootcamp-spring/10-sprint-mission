package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.common.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ChannelException extends DiscodeitException {

  public ChannelException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ChannelException(ErrorCode errorCode, UUID id) {
    super(errorCode, Map.of("channelId", id));
  }

  public ChannelException(ErrorCode errorCode, Map<String, Object> detail) {
    super(errorCode, detail);
  }
}
