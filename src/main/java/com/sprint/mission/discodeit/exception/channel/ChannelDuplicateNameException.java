package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelDuplicateNameException extends ChannelException {

  public ChannelDuplicateNameException() {
    super(ErrorCode.DUPLICATION_CHANNEL_NAME);
  }
}
