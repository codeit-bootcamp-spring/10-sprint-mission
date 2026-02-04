package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private final UUID userId;
    private final UUID channelId;
    private Instant updatedAt;
    private UUID lastReadMessageId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public void updateLastReadMessage(UUID lastReadMessageId) {
        Optional.ofNullable(lastReadMessageId)
                .filter(id -> !id.equals(this.lastReadMessageId))
                .ifPresent(id -> {
                    this.lastReadMessageId = id;
                    this.updatedAt = Instant.now();
                });
    }
}
