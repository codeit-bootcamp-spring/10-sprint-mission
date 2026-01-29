package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserLoginDto(
        @NotNull
        String username,
        @NotNull
        String password
) {
}
