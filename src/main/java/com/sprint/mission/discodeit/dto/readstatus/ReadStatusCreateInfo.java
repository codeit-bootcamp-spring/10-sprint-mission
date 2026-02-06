package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ReadStatusCreateInfo(
        UUID userId,
        UUID channelId
) {
}
