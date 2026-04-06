package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class DuplicateChannelNameException extends ChannelException {
    public DuplicateChannelNameException(String channelName) {
        super(ErrorCode.DUPLICATE_CHANNEL_NAME, Map.of("channelName", channelName));
    }
}
