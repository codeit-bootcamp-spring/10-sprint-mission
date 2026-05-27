package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class PrivateChannelParticipantRequiredException extends ChannelException {

    public PrivateChannelParticipantRequiredException() {
        super(ErrorCode.PRIVATE_CHANNEL_PARTICIPANT_REQUIRED, "participants", "null Or Empty");
    }
}
