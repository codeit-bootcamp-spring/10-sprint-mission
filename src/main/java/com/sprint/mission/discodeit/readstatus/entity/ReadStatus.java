package com.sprint.mission.discodeit.readstatus.entity;

import com.sprint.mission.discodeit.common.CommonEntity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends CommonEntity {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadAt() {
        lastReadAt = Instant.now();
    }
}
