package com.sprint.mission.discodeit.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull(message = "username은 null일 수 없습니다.")
        @NotBlank(message = "username은 공백이 될 수 없습니다.")
        String username,
        @NotNull(message = "password는 null일 수 없습니다.")
        @NotBlank(message = "password는 공백이 될 수 없습니다.")
        String password
) { }