package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ReadStatus extends BaseDomain{
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID channelId, UUID userId) {
        super();
        this.channelId = channelId;
        this.userId = userId;
    }
}
