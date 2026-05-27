package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class PrivateChannelCannotBeUpdatedException extends ChannelException {

    public PrivateChannelCannotBeUpdatedException(UUID channelId) {
        super(ErrorCode.PRIVATE_CHANNEL_CANNOT_BE_UPDATED, "channelId", channelId);
    }
}
