package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequestDto(
        @NotBlank
        UUID userId,
        @NotBlank
        UUID channelId,

        Instant lastReadAt
) {
}
