package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends DefaultEntity{
    private final UUID userID;

    private Instant lastOnlineTime;

    public UserStatus(UUID userID) {
        this.userID = userID;
        this.lastOnlineTime = Instant.now();//생성 시점을 첫 접속으로 설정
    }

    public void userOnline(){
        this.lastOnlineTime = Instant.now(); //접속했음을 알리는 메서드.
    }

    public Boolean isOnline(){
        if(lastOnlineTime == null) return false;

        return !Instant.now().minusSeconds(360).isAfter(lastOnlineTime);
    }

}
