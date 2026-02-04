package com.sprint.mission.discodeit.dto;

public class AuthDto {
    public record LoginRequest(
            String username,
            String password
    ) {}
}
