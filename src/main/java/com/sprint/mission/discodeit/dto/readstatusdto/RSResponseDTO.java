package com.sprint.mission.discodeit.dto.readstatusdto;

import java.time.Instant;
import java.util.UUID;

public record RSResponseDTO(
        UUID id,
        UUID channelId,
        UUID userId,
        Instant lastReadAt,
        Instant createdAt,
        Instant updatedAt

) {
}
