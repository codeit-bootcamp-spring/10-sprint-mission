package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ChannelDuplicateException extends ChannelException {

    public ChannelDuplicateException() {
        super(ErrorCode.DUPLICATE_CHANNEL, Instant.now(), Map.of());
    }

    public ChannelDuplicateException(UUID channelId) {
        super(ErrorCode.DUPLICATE_CHANNEL, Instant.now(), Map.of("channelId", channelId));
    }

}
