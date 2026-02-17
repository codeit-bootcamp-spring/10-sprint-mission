package com.sprint.mission.discodeit.dto.userdto;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    UUID profileId,
    String username,
    String email,
    Boolean online,
    Instant createdAt,
    Instant updatedAt
){}
