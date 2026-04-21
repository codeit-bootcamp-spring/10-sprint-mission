package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException{
    public ChannelNotFoundException(UUID channelId) {
        super(ErrorCode.CHANNEL_NOT_FOUND, Map.of(
                "resourceId", channelId.toString(),
                "operation", "ACCESS_CHANNEL",
                "currentState", "CHANNEL_NOT_FOUND",
                "rule", "요청한 ID의 채널이 시스템에 존재해야 함"
        ));
    }
}
