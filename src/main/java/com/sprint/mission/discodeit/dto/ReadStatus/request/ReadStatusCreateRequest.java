package com.sprint.mission.discodeit.dto.ReadStatus.request;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
) {
}
