package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.common.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentException extends DiscodeitException {

  public BinaryContentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BinaryContentException(ErrorCode errorCode, UUID id) {
    super(errorCode, Map.of("binaryContentId", id));
  }
}
