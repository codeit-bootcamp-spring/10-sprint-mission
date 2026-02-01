package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID userStatusId,

        @NotNull(message = "lastOnlineTime이 null로 입력되었습니다.")
        Instant lastOnlineTime
) {
}
