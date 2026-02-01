package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReadStatusCreateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID userId,

        @NotNull(message = "ID가 null입니다.")
        UUID channelId
        ) {
}
