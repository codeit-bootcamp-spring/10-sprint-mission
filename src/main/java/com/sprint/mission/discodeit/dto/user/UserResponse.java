package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

// 유저 조회 결과 반환용 -> find/findAll/create,update 반환값
public record UserResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String userName,
        String alias,
        String email,
        UUID profileId,
        Boolean online

) {
}
