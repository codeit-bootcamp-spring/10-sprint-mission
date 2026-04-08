package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateReadStatusRequestDTO(
        @NotNull(message = "userId는 null일 수 없습니다.")
        UUID userId,
        @NotNull(message = "channelId는 null일 수 없습니다.")
        UUID channelId,
        @NotNull(message = "lastReadAt은 null일 수 없습니다.")
        Instant lastReadAt
) { }
