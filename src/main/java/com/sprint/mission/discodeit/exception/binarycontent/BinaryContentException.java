package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.message.MessageException;

public abstract class BinaryContentException extends DiscodeitException {

  protected BinaryContentException(ErrorCode errorCode) {
    super(errorCode);
  }

}
