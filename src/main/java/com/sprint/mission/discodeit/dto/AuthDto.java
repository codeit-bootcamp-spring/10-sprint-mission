package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public class AuthDto {
    public record LoginRequest(
            String username,
            String password
    ) {}

    public record LoginResponse(
            UUID id,
            String username,
            String email
    ) {}
}
