package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class PrivateChannelUpdateException extends DiscodeitException {

    public PrivateChannelUpdateException(Map<String, Object> details) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE, Instant.now(), details);
    }
}
