package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateNotAllowedException extends ChannelException{
    private PrivateChannelUpdateNotAllowedException(Map<String, Object> details) {
        super(ErrorCode.INVALID_PRIVATE_CHANNEL_MODIFIED, details);
    }

    public static PrivateChannelUpdateNotAllowedException withId(UUID channelId) {
        return new PrivateChannelUpdateNotAllowedException(Map.of(
                "reason", "비공개 채널은 수정할 수 없습니다.",
                "channelId", channelId));
    }

}
