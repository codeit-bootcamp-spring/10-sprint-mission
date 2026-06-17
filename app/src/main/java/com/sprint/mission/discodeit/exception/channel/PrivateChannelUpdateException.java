package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class PrivateChannelUpdateException extends ChannelException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.CANNOT_UPDATE_PRIVATE_CHANNEL;

  public PrivateChannelUpdateException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public PrivateChannelUpdateException() {
    super(DEFAULT_CODE);
  }

  public PrivateChannelUpdateException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public PrivateChannelUpdateException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
