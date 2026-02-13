package com.sprint.mission.discodeit.dto.userstatusdto;

import java.time.Instant;
import java.util.UUID;

public record UserStateRequestDTO(
        UUID id,
        UUID userID,
        Instant lastActiveAt
) {


}
