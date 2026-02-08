package com.sprint.mission.discodeit.dto.message.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID messageId,
        UUID channelId,
        UUID authorId,
        Instant createdAt,
        Instant updatedAt,
        String content,
        List<UUID> attachmentIds
) {
}
