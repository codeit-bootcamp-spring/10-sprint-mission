package com.sprint.mission.discodeit.common.exception.channel;

import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;

public class ChannelCantUpdatePrivateException extends ChannelException {

  public ChannelCantUpdatePrivateException(Map<String, Object> details) {
    super(ErrorCode.CHANNEL_CANT_UPDATE_PRIVATE, details);
  }

}
