package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID userId,
        String username,
        String email,
        UUID profileId,
        boolean isOnline,
        Instant createdAt,
        Instant updatedAt
) {
}
