package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DuplicateEmailException extends UserException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.DUPLICATE_EMAIL;

  public DuplicateEmailException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public DuplicateEmailException() {
    super(DEFAULT_CODE);
  }

  public DuplicateEmailException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public DuplicateEmailException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
