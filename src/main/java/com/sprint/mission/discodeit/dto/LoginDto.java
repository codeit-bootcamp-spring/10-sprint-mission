package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    public record LoginRequest(
            @NotBlank
            String username,
            @NotBlank
            String password
    ) {}
}
