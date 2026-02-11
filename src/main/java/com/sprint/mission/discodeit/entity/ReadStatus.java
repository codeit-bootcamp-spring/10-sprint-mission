package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인
// 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용
// 채널별 마지막 메시지를 읽은 시간? -> lastReadTime을 필드로?
// 읽지 않은 메시지? -> ReadStatus의 lastReadTime과 메시지 createdAt,updatedAt과 비교?
@Getter
public class ReadStatus implements Serializable {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;// User의 id를 참조
    private UUID channelId;// channel의 id를 참조
    private Instant lastReadTime;

    // private 채널 생성할 때 채널에 참여하는 User의 정보를 받아 User별 ReadStatus 정보를 생성함
    public ReadStatus(UUID userId, UUID channelId, Instant lastReadTime) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.lastReadTime = lastReadTime;
    }
    
    // lastReadTime을 업데이트 하기 위한 메소드
    public void updateLastReadTime(Instant lastReadTime) {
        if (lastReadTime != null) {
            this.lastReadTime = lastReadTime;
            this.updatedAt = Instant.now();
        }
    }
}
