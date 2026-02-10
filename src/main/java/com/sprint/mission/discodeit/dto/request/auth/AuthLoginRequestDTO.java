package com.sprint.mission.discodeit.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginRequestDTO {
    @NotNull
    private UUID userId;

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;
}
