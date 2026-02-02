package com.sprint.mission.discodeit.dto.userstatusdto;

import java.time.Instant;
import java.util.UUID;

public record UserStateResponseDTO(
        UUID id,
        UUID userId,
        Instant lastActiveAt
) {
}
