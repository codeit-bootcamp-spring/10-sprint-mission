package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public final class UserStatusDto {
    private UserStatusDto() {}

    public record userStatusCreateRequest(UUID userId) { }
    public record userStatusUpdateRequest(@JsonProperty("newLastActiveAt") Instant lastActiveAt) { }
    public record userStatusResponse(@JsonProperty("id") UUID uuid, Instant createdAt, Instant updatedAt,
                                     UUID userId, Instant lastActiveAt, @JsonProperty("online") boolean isOnline) { }
}
