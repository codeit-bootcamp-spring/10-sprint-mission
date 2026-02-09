package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString
public class MessageResponse {
    private UUID id;
    private String content;
    private UUID authorId;
    private UUID channelId;
    private List<UUID> attachmentIds;
    private Instant createdAt;
    private Instant updatedAt;

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
