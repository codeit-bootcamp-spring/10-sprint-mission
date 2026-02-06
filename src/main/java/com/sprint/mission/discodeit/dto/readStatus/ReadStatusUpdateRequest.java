package com.sprint.mission.discodeit.dto.readStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID userId,
        UUID channelId,
        UUID id,
        Instant lastReadAt
) {
}
