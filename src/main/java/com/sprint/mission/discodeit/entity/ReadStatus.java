package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity{
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadTime;

    public ReadStatus(UUID userId, UUID channelId){
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = Instant.now();
    }

    // 읽은 시간 최신화
    public void updateReadTime(){
        this.lastReadTime = Instant.now();
    }
}
//구현: 채널과 1대1연관(채널은 이 엔티티를 안가지고 있어도 됨),사용자와도 1대1연관 채널의 메시지를 생성 시간을 비교과 채널의 사용자들의 최종 접속 시간을 비교
// 만 사용자의 접속시간이 메시지의 생성시간보다 뒤에 있다면 읽음 처리 --> 흐름: 채널에