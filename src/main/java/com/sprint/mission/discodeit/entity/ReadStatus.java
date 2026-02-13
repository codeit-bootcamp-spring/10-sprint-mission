package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Locked;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    //사용자가 채널별 마지막으로 메시지를 읽은 시간을 표현하는 도메인
    //사용자별 각 채널에 읽지않은 메시지를 확인하기 위해 활용
    //(userId,channelId) 당 1개 생성되는 도메인

    private UUID id;
    private Instant createdAt; //사용자가 채널에 처음 참여했을때
    private Instant updatedAt; //마지막으로 읽은 메시지의 시간
    //
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.channelId = channelId;
    }

    //메시지 읽었다는 마크
    public void markAsRead(){
        this.updatedAt = Instant.now();
    }

}
