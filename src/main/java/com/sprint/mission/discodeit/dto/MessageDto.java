package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        String content,
        UUID channelId,
        UserDto author,
        List<BinaryContentDto> attachments,
        Instant createdAt,
        Instant updatedAt
) {
}

