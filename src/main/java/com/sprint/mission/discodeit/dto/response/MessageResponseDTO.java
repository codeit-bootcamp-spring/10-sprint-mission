package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MessageResponseDTO {
    private UUID id;
    private UUID authorId;
    private UUID channelId;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;
    private MessageType messageType;
    private List<UUID> attachmentIds;
}
