package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class NotPrivateChannelMemberException extends ChannelException{
    public NotPrivateChannelMemberException(UUID userId, UUID channelId){
        super(ErrorCode.NOT_PRIVATE_CHANNEL_MEMBER_EXCEPTION, Map.of("userId", userId, "channelId", channelId));
    }
}
