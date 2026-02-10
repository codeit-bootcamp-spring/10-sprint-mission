package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.MessageType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record MessageResponseDTO (
    UUID id,
    UUID authorId,
    UUID channelId,
    String message,
    Instant createdAt,
    Instant updatedAt,
    MessageType messageType,
    List<UUID> attachmentIds
) {

}
