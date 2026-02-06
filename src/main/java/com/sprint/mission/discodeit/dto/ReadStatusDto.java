package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {
    public record CreateRequest(
            @NotNull
            UUID userId,
            @NotNull
            UUID channelId,
            Instant lastReadAt
    ) {}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {}

    public record UpdateRequest(
            Instant newLastReadAt
    ) {}
}
