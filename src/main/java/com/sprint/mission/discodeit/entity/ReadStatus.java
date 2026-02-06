package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId){
        this.userId = userId;
        this.channelId = channelId;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.lastReadAt = createdAt;
    }

    public void updateLastRead() {
        this.lastReadAt = Instant.now();
        this.updatedAt = this.lastReadAt;
    }
}
