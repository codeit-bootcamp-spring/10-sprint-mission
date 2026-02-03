package com.sprint.mission.discodeit.dto.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId,
        UserStatus.Status status
) {
}
