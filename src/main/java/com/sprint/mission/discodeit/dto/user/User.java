package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record User(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    String password,
    UUID profileId
) {

}
