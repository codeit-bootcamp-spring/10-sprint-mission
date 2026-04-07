package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class BadRequestException extends DiscodeitException {

  public BadRequestException() {
    super(ErrorCode.BAD_REQUEST);

  }

}
