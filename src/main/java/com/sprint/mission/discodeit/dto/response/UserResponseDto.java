package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        UUID profileId,
        String username,
        String email,
        Boolean online,
        Instant createdAt,
        Instant updatedAt
) { }
