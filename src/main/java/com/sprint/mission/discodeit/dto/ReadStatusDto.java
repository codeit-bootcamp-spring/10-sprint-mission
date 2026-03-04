package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public final class ReadStatusDto {
    private ReadStatusDto() {}

    public record readStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) { }
    public record readStatusUpdateRequest(Instant newLastReadAt) { }
    public record readStatusResponse(@JsonProperty("id") UUID uuid, Instant createdAt, Instant updatedAt,
                                     UUID userId, UUID channelId, Instant lastReadAt) { }
}
