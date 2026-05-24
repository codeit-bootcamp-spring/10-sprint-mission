package com.sprint.mission.discodeit.user.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserException extends DiscodeitException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserException(ErrorCode errorCode, UUID id) {
    super(errorCode, Map.of("userId", id));
  }

  public UserException(ErrorCode errorCode, Map<String, Object> detail) {
    super(errorCode, detail);
  }
}
