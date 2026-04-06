package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.base.DiscodeitException;

import java.util.Map;

public class ChannelException extends DiscodeitException {
    protected ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
