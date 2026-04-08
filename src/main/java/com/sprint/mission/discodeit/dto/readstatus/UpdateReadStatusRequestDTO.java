package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UpdateReadStatusRequestDTO(
        @NotNull(message = "newLastReadAt은 null일 수 없습니다.")
        Instant newLastReadAt
) { }
