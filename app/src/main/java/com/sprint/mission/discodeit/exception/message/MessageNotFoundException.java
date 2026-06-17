package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageNotFoundException extends MessageException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.MESSAGE_NOT_FOUND;

  public MessageNotFoundException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public MessageNotFoundException() {
    super(DEFAULT_CODE);
  }

  public MessageNotFoundException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public MessageNotFoundException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
