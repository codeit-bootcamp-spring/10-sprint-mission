package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import java.util.UUID;

public record MessageResponseDTO(
        UUID messageId,
        String content,
        UUID channelId,
        UUID authorId,
        List<UUID> attachmentIds
) {}
