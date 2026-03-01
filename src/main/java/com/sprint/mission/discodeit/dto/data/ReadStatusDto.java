package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto (
    UUID Id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    UUID channelId,
    Instant lastReadAt
){

}
