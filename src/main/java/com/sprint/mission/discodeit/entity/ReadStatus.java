package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

public class ReadStatus extends BaseEntity{
    @Getter
    private final UUID userId;
    @Getter
    private final UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public void lastReadAt() {
        setUpdateAt();
    }
}

