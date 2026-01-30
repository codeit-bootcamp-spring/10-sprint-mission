package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponseDTO(
        UUID messageId,
        Instant createdAt,
        UUID sentUserId,
        UUID sentChannelId,
        String content,
        List<UUID> attachmentIds
) { }
