package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ReadStatusCreateDto(
        UUID userId,
        UUID channelId
) {
}
