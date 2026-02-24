package com.sprint.mission.discodeit.dto.readstatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    UUID channelId,
    @JsonProperty("lastReadAt")
    Instant readAt
) {

}