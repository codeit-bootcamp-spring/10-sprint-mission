package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    ChannelParticipantAlreadyExistsException
    -----------------------------------------
    특정 채널에 해당 사용자가 이미 존재할 때 발생하는 예외 클래스
 */
public class ChannelParticipantAlreadyExistsException extends ChannelException {

    public ChannelParticipantAlreadyExistsException(UUID userId, UUID channelId) {
        super(
                ErrorCode.CHANNEL_PARTICIPANT_ALREADY_EXISTS,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                )
        );
    }
}
