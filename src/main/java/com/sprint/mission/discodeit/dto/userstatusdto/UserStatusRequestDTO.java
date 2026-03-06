package com.sprint.mission.discodeit.dto.userstatusdto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusRequestDTO(
    UUID userId,
    UUID channelId,
    Instant lastActiveAt
) {


}
