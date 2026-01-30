package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends Basic implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private final UUID channelId;

    // 유저가 해당 채널에서 "마지막으로 읽은 시각"
    private Instant lastReadAt;

    // 마지막으로 읽은 메세지 -> lastReadMessageId 추가?


    public ReadStatus(UUID userId, UUID channelId) {
        super(); // id, 생성시간 발급.
        if(userId == null || channelId == null) throw new IllegalArgumentException("userId/channelId는 null일수 없습니다.");
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = this.createdAt;
    }

    // 유저가 채널을 확인했을때 호출.
    public void readNow(){
        this.lastReadAt = Instant.now();
        update();
    }

}
