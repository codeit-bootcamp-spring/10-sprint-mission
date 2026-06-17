package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreatedEvent(
        UUID channelId,
        String channelName,
        UUID senderId,
        String senderName,
        String content
) {
}
