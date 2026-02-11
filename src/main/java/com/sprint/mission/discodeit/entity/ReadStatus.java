package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity{
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        super(UUID.randomUUID(), Instant.now());
        this.userId = userId;
        this.channelId = channelId;
        lastReadAt = Instant.MIN;
    }

    public void updateLastReadAt(Instant lastReadAt){
        this.lastReadAt = lastReadAt;
        this.onUpdate();
    }

    public boolean isUnread(Instant messageCreatedAt){
        return lastReadAt.isBefore(messageCreatedAt);
    }
}
