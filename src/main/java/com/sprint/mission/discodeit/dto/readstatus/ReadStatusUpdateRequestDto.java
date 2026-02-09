package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequestDto(
        UUID channelId,
        UUID userId,
        Instant lastRead
) {
}
