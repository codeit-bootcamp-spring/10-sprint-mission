package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ReadStatusCreateRequestDto(
    UUID userId,
    UUID channelId
) {
}
