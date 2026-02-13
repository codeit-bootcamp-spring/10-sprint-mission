package com.sprint.mission.discodeit.dto.readstatus.input;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReadStatusCreateInput(
        UUID userId,

        @NotNull(message = "ID가 null입니다.")
        UUID channelId
        ) {
}
