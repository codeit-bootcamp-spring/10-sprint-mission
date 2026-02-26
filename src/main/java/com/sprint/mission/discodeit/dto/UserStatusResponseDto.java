package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    Instant lastActivateAt,
    boolean online
) {

}
