package com.sprint.mission.discodeit.readstatus.dto;

import java.util.UUID;

public record ReadStatusCreateInfo(
        UUID userId,
        UUID channelId
) {
}
