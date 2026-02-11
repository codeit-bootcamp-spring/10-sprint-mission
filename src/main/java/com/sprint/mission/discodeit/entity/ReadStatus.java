package com.sprint.mission.discodeit.entity;

import jdk.jfr.StackTrace;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// 사용자가 채널 별로 가장 최근 메시지를 읽은 시간을 나타냄
@Getter
public class ReadStatus extends Base{
    private final UUID userID;
    private final UUID channelID;
    Instant lastReadTime;

    public ReadStatus(UUID userID, UUID channelID) {
        super();
        this.userID = userID;
        this.channelID = channelID;
        lastReadTime = Instant.now();
    }

    public void updateLastReadTime() {
        lastReadTime = Instant.now();
        updateUpdatedAt();
    }

}
