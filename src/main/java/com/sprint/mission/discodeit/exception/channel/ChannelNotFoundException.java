package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelNotFoundException extends ChannelException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.CHANNEL_NOT_FOUND;

  public ChannelNotFoundException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public ChannelNotFoundException() {
    super(DEFAULT_CODE);
  }

  public ChannelNotFoundException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public ChannelNotFoundException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
