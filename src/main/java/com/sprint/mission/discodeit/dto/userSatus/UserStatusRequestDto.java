package com.sprint.mission.discodeit.dto.userSatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusRequestDto(
        Instant createdAt,//최초 로그인 시간
        Instant updatedAt,
        //
        UUID userId
) {
}
