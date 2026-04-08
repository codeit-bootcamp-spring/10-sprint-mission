package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record LoginResponseDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    String password,
    UUID profileId
) {

}
