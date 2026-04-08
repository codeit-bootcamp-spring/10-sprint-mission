package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelNotEditableException extends ChannelException {

  public PrivateChannelNotEditableException() {
    super(ErrorCode.PRIVATE_CHANNEL_NOT_EDITABLE);
  }
}
