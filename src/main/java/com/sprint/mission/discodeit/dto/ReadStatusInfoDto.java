package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusInfoDto(
        UUID id,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
