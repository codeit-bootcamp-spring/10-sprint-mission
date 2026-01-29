package com.sprint.mission.discodeit.dto;

public record UpdateUserRequest(
        String username,
        String password,
        String email,
        byte[] image
) {
}
