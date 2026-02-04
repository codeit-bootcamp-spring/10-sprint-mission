package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

// 클라이언트에게 반환할 유저 정보
public record UserResponse(
        UUID id,
        String name,
        String nickname,
        String email,
        UUID profileId,
        boolean isOnline,
        Instant createdAt,
        Instant updatedAt
) {
}
