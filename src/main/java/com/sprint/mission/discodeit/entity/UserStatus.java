package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity{

    private final UUID userId;
    private Instant lastOnlineAt;

    public UserStatus(UUID userId){
        this.userId = userId;
    }

    // 최신 접속 시간 갱신
    public void updateAccessTime(){
        this.lastOnlineAt = Instant.now();
    }

}
