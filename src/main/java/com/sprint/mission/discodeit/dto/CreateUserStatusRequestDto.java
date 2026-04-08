package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateUserStatusRequestDto(
        @NotNull(message = "유저 ID는 필수입니다.")
        UUID userId
) {
}
