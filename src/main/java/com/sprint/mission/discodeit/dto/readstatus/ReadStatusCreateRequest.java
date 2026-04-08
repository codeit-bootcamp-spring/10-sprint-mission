package com.sprint.mission.discodeit.dto.readstatus;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
        @NotNull(message = "유저를 확인할 수 없습니다.")
        UUID userId,

        @NotNull(message = "채널을 확인할 수 없습니다.")
        UUID channelId,

        @NotNull(message = "마지막 읽은 시각을 확인할 수 없습니다.")
        Instant lastReadAt
) {

}
