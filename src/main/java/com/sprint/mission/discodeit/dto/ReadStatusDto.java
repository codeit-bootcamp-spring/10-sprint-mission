package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public final class ReadStatusDto {
    private ReadStatusDto() {}

    public record createRequest(UUID userId, UUID channelId) { }
    public record updateRequest(UUID uuid) { }
    public record response(UUID uuid, Instant createdAt, Instant updatedAt,
                           UUID userId, UUID channelId, Instant lastReadAt) { }
}
