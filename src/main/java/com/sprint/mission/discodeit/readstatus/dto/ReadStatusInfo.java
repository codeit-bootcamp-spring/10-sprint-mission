package com.sprint.mission.discodeit.readstatus.dto;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusInfo(
        UUID statusId,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {
}
