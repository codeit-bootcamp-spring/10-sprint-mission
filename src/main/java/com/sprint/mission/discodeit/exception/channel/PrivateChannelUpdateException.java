package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {
    public PrivateChannelUpdateException(UUID channelId) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE, Map.of(
                "resourceId", channelId.toString(),
                "operation", "UPDATE_CHANNEL_INFO",
                "currentState", "PRIVATE_TYPE_RESTRICTED",
                "rule", "프라이빗 채널은 생성 후 특정 속성(타입 등)을 변경할 수 없음"
        ));
    }
}
