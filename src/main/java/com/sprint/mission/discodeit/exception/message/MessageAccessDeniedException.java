package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class MessageAccessDeniedException extends MessageException {
    private MessageAccessDeniedException(Map<String, Object> details) {
        super(ErrorCode.MESSAGE_ACCESS_DENIED, details);
    }

    public static MessageAccessDeniedException privateChannel(UUID userId, UUID channelId) {
        return new MessageAccessDeniedException(Map.of(
                "userId", userId,
                "channelId", channelId,
                "reason", "비공개 채널에는 멤버만 메시지를 작성할 수 있습니다."
        ));
    }
}
