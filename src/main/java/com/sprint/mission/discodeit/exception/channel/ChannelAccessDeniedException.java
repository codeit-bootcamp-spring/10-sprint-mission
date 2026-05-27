package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelAccessDeniedException extends ChannelException {

  public ChannelAccessDeniedException(Map<String, Object> details) {
    super(ErrorCode.CHANNEL_ACCESS_DENIED, details);
  }
}
