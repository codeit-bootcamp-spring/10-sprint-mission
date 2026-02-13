package com.sprint.mission.discodeit.dto.ReadStatus.response;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
        UUID readStatusID,
        Instant lastReadTime
) {}
