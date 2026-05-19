package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {

    public ChannelNotFoundException() {
        super(ErrorCode.CHANNEL_NOT_FOUND);
    }

    public ChannelNotFoundException(UUID channelId) {
        super(ErrorCode.CHANNEL_NOT_FOUND, Map.of("channelId", channelId));
    }
}
