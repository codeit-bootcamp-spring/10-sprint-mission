package com.sprint.mission.discodeit.message.exception;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.exception.ErrorCode;
import java.util.UUID;

public class MessageException extends DiscodeitException {

  public MessageException(ErrorCode errorCode, UUID id) {
    super(errorCode, id);
  }
}
