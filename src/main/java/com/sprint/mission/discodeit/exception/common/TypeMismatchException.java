package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class TypeMismatchException extends DiscodeitException {

  public TypeMismatchException() {
    super(ErrorCode.TYPE_MISMATCH);
  }

}
