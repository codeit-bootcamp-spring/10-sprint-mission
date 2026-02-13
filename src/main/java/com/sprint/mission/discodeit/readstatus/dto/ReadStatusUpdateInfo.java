package com.sprint.mission.discodeit.readstatus.dto;

import java.util.UUID;

public record ReadStatusUpdateInfo(
        UUID userId,
        UUID channelId
) {
}
