package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.StatusType;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDto(
        UUID userId,
        Instant createdAt,
        Instant updatedAt,
        String userName,
        StatusType status,
        String email,
        UUID profileId
) {
}
