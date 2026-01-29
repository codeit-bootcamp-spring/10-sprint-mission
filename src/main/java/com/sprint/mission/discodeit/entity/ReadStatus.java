package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private String id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId; //누가
    private UUID channelId; //어떤 채널에서
    private Instant lastReadAt; //마지막으로 몇시에 읽었는지

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    //읽은 시간 업데이트
    public void updateLastReadAt() {
        this.lastReadAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
