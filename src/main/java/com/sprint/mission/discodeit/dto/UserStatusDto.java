package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public final class UserStatusDto {
    private UserStatusDto() {}

    public record createRequest(UUID userId) { }
    public record updateRequest(Instant lastActiveAt) { }
    public record response(UUID uuid, Instant createdAt, Instant updatedAt,
                           boolean isOnline) { }
}
