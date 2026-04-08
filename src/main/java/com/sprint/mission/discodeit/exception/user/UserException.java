package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.common.ErrorCode;
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
