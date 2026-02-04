package com.sprint.mission.discodeit.dto.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID readStatusID,
        Instant lastReadTime
) {
}
