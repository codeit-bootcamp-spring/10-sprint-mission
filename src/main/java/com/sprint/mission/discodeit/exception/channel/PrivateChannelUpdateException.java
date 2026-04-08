package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(String message) {
        super(ErrorCode.CHANNEL_UPDATE_FORBIDDEN, message);
    }

    public PrivateChannelUpdateException(String message, Map<String, Object> details) {
        super(ErrorCode.CHANNEL_UPDATE_FORBIDDEN, message, details);
    }
}
