package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MessageEmptyException extends MessageException {

  public MessageEmptyException() {
    super(ErrorCode.MESSAGE_EMPTY);
  }

}
