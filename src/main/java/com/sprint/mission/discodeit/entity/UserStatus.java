package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity implements Serializable {
    private UUID userID;
    private Instant lastActiveAt;

    public UserStatus(UUID userID) {
        this.userID = userID;
        this.lastActiveAt = Instant.now();
    }

    public boolean isOnline(){
        return (Duration.between(lastActiveAt, Instant.now()).getSeconds() < 300);
    }

    public void update(Instant lastActivateAt){
        this.lastActiveAt = lastActivateAt;
    }
}
