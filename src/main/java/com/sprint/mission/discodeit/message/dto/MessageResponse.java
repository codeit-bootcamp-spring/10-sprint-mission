package com.sprint.mission.discodeit.message.dto;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentResponse> attachments

) {
}
