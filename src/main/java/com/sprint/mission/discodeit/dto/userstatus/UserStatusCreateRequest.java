package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotNull(message = "유저를 확인할 수 없습니다.")
    UUID userId,

    Instant lastActiveAt
) {

}
