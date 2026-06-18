package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.READ_STATUS_ALREADY_EXISTS;

  public ReadStatusAlreadyExistsException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public ReadStatusAlreadyExistsException() {
    super(DEFAULT_CODE);
  }

  public ReadStatusAlreadyExistsException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public ReadStatusAlreadyExistsException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
