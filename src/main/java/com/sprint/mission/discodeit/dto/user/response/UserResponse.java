package com.sprint.mission.discodeit.dto.user.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID userID,
        Instant createdAt,
        Instant updateAt,
        String name,
        String email,
        UUID profileId,
        Boolean online
) {}
