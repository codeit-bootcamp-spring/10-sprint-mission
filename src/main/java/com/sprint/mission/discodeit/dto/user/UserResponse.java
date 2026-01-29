package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String userName,
        String email,
        boolean isConnected,
        UUID profileId
) {
}
