package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class PasswordMismatchException extends UserException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.PASSWORD_MISMATCH;

  public PasswordMismatchException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public PasswordMismatchException() {
    super(DEFAULT_CODE);
  }

  public PasswordMismatchException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public PasswordMismatchException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
