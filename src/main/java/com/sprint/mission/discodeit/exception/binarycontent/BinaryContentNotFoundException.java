package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import java.util.Map;

public class BinaryContentNotFoundException extends BinaryContentException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.BINARY_CONTENT_NOT_FOUND;

  public BinaryContentNotFoundException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public BinaryContentNotFoundException() {
    super(DEFAULT_CODE);
  }

  public BinaryContentNotFoundException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public BinaryContentNotFoundException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
