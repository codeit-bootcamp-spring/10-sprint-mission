package com.sprint.mission.discodeit.common.exception.common;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class CommonException extends DiscodeitException {

  public CommonException(ErrorCode errorCode) {
    super(errorCode);
  }
}
