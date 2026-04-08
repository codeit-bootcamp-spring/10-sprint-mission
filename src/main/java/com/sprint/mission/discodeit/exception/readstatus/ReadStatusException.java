package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.common.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusException extends DiscodeitException {

  public ReadStatusException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ReadStatusException(ErrorCode errorCode, UUID id) {
    super(errorCode, id);
  }

  public ReadStatusException(ErrorCode errorCode,
      Map<String, ?> detail) {
    super(errorCode, detail);
  }
}
