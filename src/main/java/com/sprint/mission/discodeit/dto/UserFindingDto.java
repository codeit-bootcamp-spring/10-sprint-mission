package com.sprint.mission.discodeit.dto;

public record UserFindingDto(
        String username,
        String email,
        boolean isOnline
) {
}
