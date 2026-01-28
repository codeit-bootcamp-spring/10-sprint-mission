package com.sprint.mission.discodeit.dto.user;

public record UserFindingDto(
        String username,
        String email,
        boolean isOnline
) {
}
