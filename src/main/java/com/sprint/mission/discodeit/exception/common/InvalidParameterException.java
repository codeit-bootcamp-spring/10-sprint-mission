package com.sprint.mission.discodeit.exception.common;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class InvalidParameterException extends DiscodeitException {

  public InvalidParameterException(String name) {
    super(ErrorCode.INVALID_PARAMETER, Map.of("parameter", name));
  }

  public InvalidParameterException(String name, String message) {
    super(ErrorCode.INVALID_PARAMETER, Map.of(
        "parameter", name,
        "reason", message
    ));
  }
}
