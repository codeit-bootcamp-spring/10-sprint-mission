package com.sprint.mission.discodeit.dto.messagedto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponseDTO(
        UUID id,
        UUID channelId,
        UUID userId,
        String content,
        Instant createdAt,
        Instant updatedAt,
        List<UUID> attachments
) {
}
