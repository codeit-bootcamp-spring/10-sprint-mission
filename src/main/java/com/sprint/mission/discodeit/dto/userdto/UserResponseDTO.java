package com.sprint.mission.discodeit.dto.userdto;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String userName,
    String email,
    UUID profileId,
    boolean online
) {

}
