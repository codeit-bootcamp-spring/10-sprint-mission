package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {
    public record CreateRequest(
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {}

    public record UpdateRequest(
            UUID id,
            Instant lastReadAt
    ) {}

    public record Response(
            UUID id,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {}
}
