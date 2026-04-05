package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class MessageAccessDeniedException extends MessageException {

  public MessageAccessDeniedException(Map<String, Object> details) {
    super(ErrorCode.ACCESS_DENIED, details);
  }

}
