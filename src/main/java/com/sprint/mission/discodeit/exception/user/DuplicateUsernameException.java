package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DuplicateUsernameException extends UserException {

  private static final ErrorCode DEFAULT_CODE = ErrorCode.DUPLICATE_USERNAME;

  public DuplicateUsernameException(Map<String, Object> details, Throwable cause) {
    super(DEFAULT_CODE, details, cause);
  }

  public DuplicateUsernameException() {
    super(DEFAULT_CODE);
  }

  public DuplicateUsernameException(Map<String, Object> details) {
    super(DEFAULT_CODE, details);
  }

  public DuplicateUsernameException(Throwable cause) {
    super(DEFAULT_CODE, cause);
  }
}
