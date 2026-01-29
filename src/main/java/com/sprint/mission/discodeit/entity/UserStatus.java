package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserStatus extends DefaultEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userId;

    private Instant lastOnlineTime;

    public UserStatus(UUID userId) {
        this.userId = userId;
        this.lastOnlineTime = Instant.now();//생성 시점을 첫 접속으로 설정
    }

    public Boolean isOnline(){
        if(lastOnlineTime == null) return false;

        return !Instant.now().minusSeconds(360).isAfter(lastOnlineTime);
    }

}
