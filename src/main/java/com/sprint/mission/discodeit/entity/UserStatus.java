package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    /* 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다.
    사용자의 온라인 상태를 확인하기 위해 활용합니다.
     */
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;

    public UserStatus(UUID userId){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
    }

    public boolean isOnline(){
        Instant now = Instant.now();
        Instant limitTime = updatedAt.plusSeconds(300);
        return now.isBefore(limitTime);
    }
    public void update(){
        this.updatedAt = Instant.now();
    }
}
