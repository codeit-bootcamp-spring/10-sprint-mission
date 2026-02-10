package com.sprint.mission.discodeit.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthLoginRequestDTO (
    @NotNull
    UUID userId,

    @NotBlank
    String username,

    @NotBlank
    String password
) {

}
