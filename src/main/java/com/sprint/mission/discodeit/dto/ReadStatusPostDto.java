package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusPostDto(
    @NotNull
    UUID userId,

    @NotNull
    UUID channelId,

    @NotNull
    Instant lastReadAt
) {

}
