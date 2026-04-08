package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ChannelException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public ChannelException(
            ErrorCode errorCode,
            String message,
            Map<String, Object> details,
            Throwable cause
    ) {
        super(errorCode, message, details, cause);
    }
}
