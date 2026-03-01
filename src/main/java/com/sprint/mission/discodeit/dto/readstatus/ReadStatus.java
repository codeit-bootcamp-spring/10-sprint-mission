package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatus(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    UUID channelId,
    Instant lastReadAt
) {

}
