package com.sprint.mission.discodeit.dto.user;

public record UserResponseDto(
        String username,
        String email,
        boolean isOnline
) {
}
