package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateUserStatusRequestDTO(
        @NotNull(message = "userId는 null일 수 없습니다.")
        UUID userId,
        Instant lastLoginAt
) { }