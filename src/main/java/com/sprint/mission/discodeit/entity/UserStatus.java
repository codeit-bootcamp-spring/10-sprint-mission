package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity{
    private UUID userID;
    private Instant lastActiveAt;

    public UserStatus(UUID userID) {
        this.userID = userID;
        this.lastActiveAt = Instant.now();
    }

    public boolean isOnline(){
        return (Duration.between(lastActiveAt, Instant.now()).getSeconds() < 300);
    }



}
