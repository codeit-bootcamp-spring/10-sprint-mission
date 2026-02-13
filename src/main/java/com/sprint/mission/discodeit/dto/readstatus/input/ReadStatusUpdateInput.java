package com.sprint.mission.discodeit.dto.readstatus.input;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateInput(
        @NotNull(message = "ID가 null입니다.")
        UUID readStatusId,
        Instant lastReadTime
){
}
