package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class ChannelException extends DiscodeitException {

    protected ChannelException(ErrorCode errorCode) {
        super(errorCode, Instant.now(), Map.of());
    }

    protected ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, Instant.now(), details);
    }


}
