package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(UUID id) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE, Map.of("channelId", id));
    }
}
