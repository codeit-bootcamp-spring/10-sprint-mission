package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatus(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    Instant lastActiveAt,
    boolean online
) {

}
