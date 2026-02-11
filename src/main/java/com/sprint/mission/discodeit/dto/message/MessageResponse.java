package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String content,
        UUID senderId,
        UUID channelId,
        List<UUID> attachmentIds
) {
}
