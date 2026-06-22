package com.sprint.mission.discodeit.binarycontent.domain.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.UUID;

public class BinaryContentException extends DiscodeitException {

  public BinaryContentException(ErrorCode errorCode, UUID id) {
    super(errorCode, id);
  }
}
