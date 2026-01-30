package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateDto(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
