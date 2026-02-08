package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends Common implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID userId;
    private UUID channelId;

    private Instant readAt;

    public ReadStatus(UUID userId, UUID channelId) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.readAt = Instant.now();
    }

    // 메세지를 생성시간보다 나중에 보더라도 메세지 다 안봤을 때도

    public void update(Instant newReadAt) {
        if (newReadAt != null && !newReadAt.isAfter(this.readAt)) {
            this.readAt = newReadAt;
            this.updatedAt = Instant.now();
        }
    }

    public boolean hasRead(Instant messageCreatedAt) {
        if (messageCreatedAt == null || this.readAt == null) {
            return false;
        }
        return !messageCreatedAt.isAfter(this.readAt);
    }

}
