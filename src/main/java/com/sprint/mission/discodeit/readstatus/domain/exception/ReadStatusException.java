package com.sprint.mission.discodeit.readstatus.domain.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusException extends DiscodeitException {

  public ReadStatusException(ErrorCode errorCode, UUID id) {
    super(errorCode, id);
  }

  public ReadStatusException(ErrorCode errorCode,
      Map<String, ?> detail) {
    super(errorCode, detail);
  }

  public ReadStatusException(ErrorCode errorCode) {
    super(errorCode);
  }
}
