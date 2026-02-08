package com.sprint.mission.discodeit.dto.user.response;

import java.time.Instant;
import java.util.UUID;

public record UserWithOnlineResponse(
        UUID userId,
        Instant createdAt,
        Instant updatedAt,
        String email,
        String userName,
        String nickName,
        String birthday,
        UUID profileId,
        boolean isOnline
) {
}
