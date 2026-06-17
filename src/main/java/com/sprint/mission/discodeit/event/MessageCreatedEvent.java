package com.sprint.mission.discodeit.event;

import java.util.UUID;

/*
    MessageCreatedEvent
    -------------------------
    새로운 메시지가 등록되었음을 알리는 이벤트
 */
public record MessageCreatedEvent(
        UUID messageId,
        UUID channelId,
        UUID senderId,
        String senderName,
        String channelName,
        String content
) {
}