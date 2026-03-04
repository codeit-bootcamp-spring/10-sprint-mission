package com.sprint.mission.discodeit.dto.userstatusdto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDTO(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

}
