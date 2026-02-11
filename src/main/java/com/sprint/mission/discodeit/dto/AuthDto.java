package com.sprint.mission.discodeit.dto;

public class AuthDto {

    public record LoginRequest(
            String userName,
            String password
    ) {
    }

    public record LogoutRequest(
            String userName
    ) {
    }
}
