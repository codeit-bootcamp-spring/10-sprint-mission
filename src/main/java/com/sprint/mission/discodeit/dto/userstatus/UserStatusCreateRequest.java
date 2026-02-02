package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserStatusCreateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID userId
) {
}
