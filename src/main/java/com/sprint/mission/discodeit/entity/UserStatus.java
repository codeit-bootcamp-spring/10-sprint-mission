package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;


public class UserStatus extends DefaultEntity{
    @Getter
    private final UUID userID;

    private Instant lastOnlineTime;

    public UserStatus(UUID userID) {
        this.userID = userID;
    }

    public void userOnline(){
        this.lastOnlineTime = Instant.now();
    }

    public Boolean isOnline(){
        if(lastOnlineTime == null) return false;

        return !Instant.now().minusSeconds(360).isAfter(lastOnlineTime);
    }

}
