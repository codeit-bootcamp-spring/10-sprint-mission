package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity{
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadTime;

    public ReadStatus(UUID userId, UUID channelId){
        this.userId = userId;
        this.channelId = channelId;
    }

    // 읽은 시간 최신화
    public void updateReadTime(){
        this.lastReadTime = Instant.now();
    }
}
