package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
        UUID id,
        UUID userId,
        Instant lastActiveAt
) {
    public record UserStatusCreateRequest(UUID userId) { }
    public record UserStatusUpdateRequest(Instant newLastActiveAt) { }
}