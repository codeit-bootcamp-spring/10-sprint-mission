package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity{
    @Setter
    private UUID userId;
    @Setter
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void setLastReadAt() {
        lastReadAt = Instant.now();
        updatedAt = lastReadAt;
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "userId=" + userId +
                ", channelId=" + channelId +
                ", lastReadAt=" + lastReadAt +
                '}';
    }
}
