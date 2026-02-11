package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponseDTO(
        UUID statusId,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) { }
