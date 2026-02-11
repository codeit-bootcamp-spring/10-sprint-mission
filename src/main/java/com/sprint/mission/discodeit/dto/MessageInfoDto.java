package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageInfoDto(
        UUID id,
        UUID senderId,
        UUID channelId,
        String content,
        Instant updatedAt,
        List<UUID> attachmentIds
) {
}
