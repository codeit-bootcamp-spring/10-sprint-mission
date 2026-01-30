package com.sprint.mission.discodeit.dto.auth;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        UUID id,
        String userName,
        String email,
        boolean online,
        Instant lastSeenAt,
        UUID profileImageId
) {}
