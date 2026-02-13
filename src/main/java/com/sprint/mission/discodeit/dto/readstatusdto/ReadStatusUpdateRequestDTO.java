package com.sprint.mission.discodeit.dto.readstatusdto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequestDTO(
        UUID id,
        UUID channelId,
        UUID userId,
        Instant lastReadAt,
        Instant updatedAt) {
}
