package com.sprint.mission.discodeit.dto.userStatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID id,
        UserStatus.Status status
) {
}
