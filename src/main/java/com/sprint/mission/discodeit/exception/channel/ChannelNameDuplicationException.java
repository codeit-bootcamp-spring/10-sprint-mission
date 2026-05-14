package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class ChannelNameDuplicationException extends ChannelException {

    public ChannelNameDuplicationException(String channelName) {
        super(ErrorCode.CHANNEL_NAME_DUPLICATE, Map.of("channelName", channelName));
    }

    public ChannelNameDuplicationException() {
        super(ErrorCode.CHANNEL_NAME_DUPLICATE);
    }


}
