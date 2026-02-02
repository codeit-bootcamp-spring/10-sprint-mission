package com.sprint.mission.discodeit.dto.userstatusdto;

import java.time.Instant;
import java.util.UUID;

public record USRequestDTO(
        UUID id,
        UUID userID,
        Instant lastActiveAt
) {


}
