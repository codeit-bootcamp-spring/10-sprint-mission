package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.ToString;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class MessageResponse {
    private final UUID id;
    private final String content;
    private final UUID authorId;
    private final UUID channelId;
    private final List<UUID> attachmentIds;
    private final Instant createdAt;
    private final Instant updatedAt;

    public MessageResponse(UUID id, String content, UUID authorId, UUID channelId, List<UUID> attachmentIds, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
