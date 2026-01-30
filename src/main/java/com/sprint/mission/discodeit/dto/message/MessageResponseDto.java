package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageResponseDto(
        UUID id,
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachments
) {
}
