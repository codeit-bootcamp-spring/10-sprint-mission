package com.sprint.mission.discodeit.message.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
