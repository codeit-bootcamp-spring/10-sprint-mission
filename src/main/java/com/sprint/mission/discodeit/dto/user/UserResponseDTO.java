package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO (
        UUID userId,
        String email,
        String username,

        String userStatus,
        Instant lastLoginAt,

        UUID profileImageId
){ }
