package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

// 클라이언트에게 반환할 유저 정보
public record UserDto(
        UUID id,
        String username,
        String nickname,
        String email,
        UUID profileId,
        boolean online,
        Instant createdAt,
        Instant updatedAt
) {
}
