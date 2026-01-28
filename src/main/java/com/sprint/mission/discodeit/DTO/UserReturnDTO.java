package com.sprint.mission.discodeit.DTO;

import java.time.Instant;
import java.util.UUID;

public record UserReturnDTO(
    UUID id,
    UUID profileID,
    String userName,
    String email,
    boolean online,
    Instant lastActivedAt,
    Instant CreatedAt,
    Instant UpdatedAt
){}
