package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReadStatusCreateDto(
        @NotNull
        UUID userId,
        @NotNull
        UUID channelId
) {
}
