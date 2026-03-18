package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        BinaryContentDto profile,
        Boolean online
) {
    public record UserLoginRequest(String username, String password) { }
    public record UserCreateRequest(String username, String password, String email) { }
    public record UserUpdateRequest(String newUsername, String newPassword, String newEmail) { }
}
