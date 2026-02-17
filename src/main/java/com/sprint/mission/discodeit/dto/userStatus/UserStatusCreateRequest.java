package com.sprint.mission.discodeit.dto.userStatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
        @NotNull(message = "사용자가 필요합니다.")
        UUID userId,

        Instant lastActiveAt
) {
}
