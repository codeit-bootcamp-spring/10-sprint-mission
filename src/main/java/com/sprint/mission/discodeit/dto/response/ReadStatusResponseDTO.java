package com.sprint.mission.discodeit.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ReadStatusResponseDTO (
    UUID id,
    UUID userId,
    UUID channelId,
    Instant createdAt,
    Instant updatedAt,
    Instant lastReadTime
) {

}
