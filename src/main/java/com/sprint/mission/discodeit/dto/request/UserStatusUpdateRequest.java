package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotNull(message = "newLastActiveAt는 필수입니다.")
    Instant newLastActiveAt
) {

}
