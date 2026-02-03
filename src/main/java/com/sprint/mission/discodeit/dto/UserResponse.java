package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserOnlineStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        UserOnlineStatus userOnlineStatus
) {
    public static UserResponse of(User user, UserOnlineStatus status) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                status
        );
    }
}
