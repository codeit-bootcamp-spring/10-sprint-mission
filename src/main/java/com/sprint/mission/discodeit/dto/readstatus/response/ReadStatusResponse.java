package com.sprint.mission.discodeit.dto.readstatus.response;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
        UUID readStatusId,
        UUID userId,
        UUID channelId,
        Instant createdAt,
        Instant updatedAt,
        Instant lastReadTime
) {
}
