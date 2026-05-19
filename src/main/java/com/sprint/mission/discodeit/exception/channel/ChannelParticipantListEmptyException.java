package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelParticipantListEmptyException extends ChannelException {

    public ChannelParticipantListEmptyException() {
        super(ErrorCode.CHANNEL_PRIVATE_CREATE_EMPTY_USERS, Map.of());
    }
}
