package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelCannotBeUpdatedException extends ChannelException {

  public PrivateChannelCannotBeUpdatedException() {
    super(ErrorCode.PRIVATE_CHANNEL_CANNOT_BE_UPDATED);
  }

}
