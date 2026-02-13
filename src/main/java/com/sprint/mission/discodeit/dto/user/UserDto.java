package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        UUID profileId,
        Boolean online,
        Instant createdAt,
        Instant updatedAt
) {
}
