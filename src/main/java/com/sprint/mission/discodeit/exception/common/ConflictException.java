package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ConflictException extends DiscodeitException {

  public ConflictException() {
    super(ErrorCode.CONFLICT);
  }

}
