package com.sprint.mission.discodeit.dto.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        UUID userId,
        String status,
        String displayName,
        Instant accessTime,
        boolean currentlyLoggedIn
) {
    public static UserStatusResponse from(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getStatus().getUserStatus(),
                userStatus.getStatus().getDisplayName(),
                userStatus.getAccessTime(),
                userStatus.isCurrentlyLoggedIn()
        );
    }
}
