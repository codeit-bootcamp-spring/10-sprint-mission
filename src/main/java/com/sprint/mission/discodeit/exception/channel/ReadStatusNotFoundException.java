package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ReadStatusNotFoundException extends ChannelException {

  public ReadStatusNotFoundException(Map<String, Object> details) {
    super(ErrorCode.READ_STATUS_NOT_FOUND, details);
  }
}
