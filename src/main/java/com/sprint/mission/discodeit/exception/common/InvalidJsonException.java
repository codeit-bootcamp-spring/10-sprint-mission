package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidJsonException extends DiscodeitException {

  public InvalidJsonException() {
    super(ErrorCode.INVALID_JSON);
  }


}
