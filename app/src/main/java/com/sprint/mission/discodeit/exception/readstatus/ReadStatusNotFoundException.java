package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ReadStatusNotFoundException extends ReadStatusException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.READ_STATUS_NOT_FOUND;

  public ReadStatusNotFoundException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public ReadStatusNotFoundException() {
    super(DEFAULT_CODE);
  }

  public ReadStatusNotFoundException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public ReadStatusNotFoundException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
