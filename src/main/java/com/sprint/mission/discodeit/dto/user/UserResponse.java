package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String nickname,
        UUID profileId,
        UserStatusResponse status
) {
    public static UserResponse from(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileId(),
                UserStatusResponse.from(userStatus)
        );
    }
}
