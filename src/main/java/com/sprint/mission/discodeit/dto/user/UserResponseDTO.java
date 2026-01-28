package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.UserStatusType;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO (
        UUID userId,
        String email,
        String username,

        UserStatusType userStatus,
        Instant lastLoginAt,

        UUID profileImageId
){ }
