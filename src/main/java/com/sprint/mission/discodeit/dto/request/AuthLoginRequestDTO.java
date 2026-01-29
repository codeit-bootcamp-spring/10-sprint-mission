package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AuthLoginRequestDTO {
    @NotNull
    private UUID userId;

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;
}
