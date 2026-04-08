package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DuplicateNameException extends ChannelException {

  public DuplicateNameException() {
    super(ErrorCode.DUPLICATE_NAME);
  }
}
