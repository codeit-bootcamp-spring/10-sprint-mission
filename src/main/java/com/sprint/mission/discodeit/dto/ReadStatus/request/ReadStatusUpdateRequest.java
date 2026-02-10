package com.sprint.mission.discodeit.dto.ReadStatus.request;

import java.time.Instant;

public record ReadStatusUpdateRequest(
        Instant lastReadTime
) {
}
