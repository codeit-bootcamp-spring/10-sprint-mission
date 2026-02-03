package com.sprint.mission.discodeit.dto.readstatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
) {
}
