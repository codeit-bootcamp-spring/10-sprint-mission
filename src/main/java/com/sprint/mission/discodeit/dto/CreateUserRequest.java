package com.sprint.mission.discodeit.dto;

public record CreateUserRequest(
        String username,
        String password,
        String email,
        byte[] image
) {
}
