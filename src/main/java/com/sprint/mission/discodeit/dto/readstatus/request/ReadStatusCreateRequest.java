package com.sprint.mission.discodeit.dto.readstatus.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReadStatusCreateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID userId
) {
}
