package com.sprint.mission.discodeit.global.web.ws.stomp.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class StompException extends DiscodeitException {

  public StompException(ErrorCode errorCode, Object key, Object value) {
    super(errorCode, key, value);
  }
}
