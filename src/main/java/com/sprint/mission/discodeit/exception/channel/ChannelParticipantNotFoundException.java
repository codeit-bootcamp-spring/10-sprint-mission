package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    ChannelParticipantNotFoundException
    -----------------------------------
    해당 채널에 특정 사용자가 존재하지 않을 때 발생하는 예외 클래스
 */
public class ChannelParticipantNotFoundException extends ChannelException {

    public ChannelParticipantNotFoundException(UUID userId, UUID channelId) {
        super(
                ErrorCode.CHANNEL_PARTICIPANT_NOT_FOUND,
                Map.of(
                        "userId", userId,
                        "channelId", channelId
                )
        );
    }
}
