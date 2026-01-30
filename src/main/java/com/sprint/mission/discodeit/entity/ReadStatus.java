package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final UUID channelId;
    private Instant readAt;

    public ReadStatus(UUID userId, UUID channelId) {
        super();
        this.readAt = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
    }

    public void updateLastReadAt(Instant time) {
        this.readAt = time;
        touch();
    }
}
