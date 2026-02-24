package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDTO(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UUID userId,
        Instant lastActiveAt,
        boolean online
) {}
