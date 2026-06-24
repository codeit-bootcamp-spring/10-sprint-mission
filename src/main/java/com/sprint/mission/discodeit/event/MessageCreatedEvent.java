package com.sprint.mission.discodeit.event;

import java.time.Instant;
import java.util.UUID;

public record MessageCreatedEvent(
        UUID messageId,
        UUID channelId,
        String channelName,
        UUID senderId,
        String senderName,
        String content,
        Instant createdAt
) {
}
