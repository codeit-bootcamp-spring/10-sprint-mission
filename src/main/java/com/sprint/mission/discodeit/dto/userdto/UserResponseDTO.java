package com.sprint.mission.discodeit.dto.userdto;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    UUID profileID,
    String userName,
    String email,
    boolean online,
    Instant lastActivedAt,
    Instant CreatedAt,
    Instant UpdatedAt
){}
