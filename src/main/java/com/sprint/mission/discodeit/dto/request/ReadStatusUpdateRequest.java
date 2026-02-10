package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID readStatusId,
        UUID userId,
        UUID channelId,
        Instant newLastReadAt
) {
}
