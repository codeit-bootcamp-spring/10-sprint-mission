package com.sprint.mission.discodeit.exception.userstatus;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserStatusException extends DiscodeitException {

  public UserStatusException(ErrorCode errorCode,
      Map<String, Object> details, Throwable cause) {
    super(errorCode, details, cause);
  }

  public UserStatusException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserStatusException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  public UserStatusException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
