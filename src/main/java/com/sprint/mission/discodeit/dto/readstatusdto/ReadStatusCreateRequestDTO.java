package com.sprint.mission.discodeit.dto.readstatusdto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequestDTO(
    @NotNull(message = "사용자 ID는 공백이거나 Null일 수 없습니다.")
    UUID userId,

    @NotNull(message = "채널 ID는 공백이거나 Null일 수 없습니다.")
    UUID channelId,

    Instant lastReadAt
) {

}
