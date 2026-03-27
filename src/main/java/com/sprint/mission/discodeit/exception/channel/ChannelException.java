package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(ErrorCode errorCode,
        Instant timestamp, Map<String, Object> details) {
        super(errorCode, timestamp, details);
    }
}
