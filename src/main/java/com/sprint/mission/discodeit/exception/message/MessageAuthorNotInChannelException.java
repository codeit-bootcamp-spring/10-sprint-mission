package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class MessageAuthorNotInChannelException extends MessageException {
    public MessageAuthorNotInChannelException(UUID authorId, UUID channelId) {
        super(ErrorCode.MESSAGE_AUTHOR_NOT_IN_CHANNEL, Map.of("authorId", authorId, "channelId", channelId));
    }
}
