package com.sprint.mission.discodeit.dto.readstatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateDTO(
        UUID readStatusId,
        Instant updatedAt
) {}
