package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class PrivateChannelParticipantException extends ChannelException {

    private PrivateChannelParticipantException(Map<String, Object> details) {
        super(ErrorCode.INVALID_PRIVATE_CHANNEL_CREATED, details);
    }

    public static PrivateChannelParticipantException minimumParticipants(int currentCount) {
        return new PrivateChannelParticipantException(Map.of(
                "reason", "비공개 채널은 최소 2명의 참여자가 필요합니다.",
                "currentCount", currentCount,
                "minimumRequired", 2));
    }
}
