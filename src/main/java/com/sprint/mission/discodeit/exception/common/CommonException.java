package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class CommonException extends DiscodeitException {

  public CommonException(ErrorCode errorCode) {
    super(errorCode);
  }
}
