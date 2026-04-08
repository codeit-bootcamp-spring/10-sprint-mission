package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UpdateStatusByUserIdRequestDTO(
        @NotNull(message = "userId는 null일 수 없습니다.")
        UUID userId,
        @NotNull(message = "newLastActiveAt은 null일 수 없습니다.")
        Instant newLastActiveAt
) { }
