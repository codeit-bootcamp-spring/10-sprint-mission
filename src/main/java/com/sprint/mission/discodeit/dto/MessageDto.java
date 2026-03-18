package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UserDto author,
        List<BinaryContentDto> attachments
) {
    public record MessageCreateRequest(UUID channelId, UUID authorId, String content) { }
    public record MessageUpdateRequest(String newContent) { }
}