package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserOnlineStatus;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
        UUID id,
        Instant lastActiveAt,
        UserOnlineStatus userOnlineStatus
) {
    public static UserStatusResponse of(UserStatus userStatus, UserOnlineStatus status) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getLastActiveAt(),
                status
        );
    }
}
