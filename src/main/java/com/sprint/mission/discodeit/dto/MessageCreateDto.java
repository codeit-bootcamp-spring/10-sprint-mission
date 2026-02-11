package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageCreateDto(
        UUID senderId,
        UUID channelId,
        String content,
        List<UUID> attachmentIds
) {
}
