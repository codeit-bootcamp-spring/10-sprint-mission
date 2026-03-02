package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@JsonPropertyOrder({"id", "createdAt", "updatedAt", "userId", "channelId", "lastReadAt"})
public class ReadStatus extends BaseEntity {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void updateLastReadAt(Instant newLastReadAt) {
        if (newLastReadAt != null) {
            this.lastReadAt = newLastReadAt;
        }
        markUpdated();
    }
}
