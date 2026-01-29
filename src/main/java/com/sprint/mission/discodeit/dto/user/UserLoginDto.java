package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserLoginDto(
        @NotEmpty
        String username,
        @NotEmpty
        String password
) {
}
