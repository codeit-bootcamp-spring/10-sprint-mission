package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String content,
        Instant createdAt, //우선 string 으,로!!! 나중에 바꿈

        UUID senderId,
        String senderName,
        String senderAlias,

        UUID channelId,
        String channelName,

        List<UUID> attachments
) { }
