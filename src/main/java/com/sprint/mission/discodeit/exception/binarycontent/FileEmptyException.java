package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class FileEmptyException extends BinaryContentException {

  public FileEmptyException() {
    super(ErrorCode.FILE_IS_EMPTY);
  }

  public FileEmptyException(Map<String, Object> details) {
    super(ErrorCode.FILE_IS_EMPTY, details);
  }
}
