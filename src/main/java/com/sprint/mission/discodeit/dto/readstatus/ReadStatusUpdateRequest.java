package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReadStatusUpdateRequest(
        @NotNull(message = "새로운 마지막 읽은 시각을 확인할 수 없습니다.")
        Instant newLastReadAt
) {

}
