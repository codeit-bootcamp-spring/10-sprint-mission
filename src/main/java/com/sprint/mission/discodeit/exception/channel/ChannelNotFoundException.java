package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    ChannelNotFoundException
    ------------------------
    해당 채널이 시스템 내 존재하지 않을 때 발생하는 예외 클래스
 */
public class ChannelNotFoundException extends ChannelException{

    public ChannelNotFoundException(UUID channelId) {
        super(
                ErrorCode.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
        );
    }
}
