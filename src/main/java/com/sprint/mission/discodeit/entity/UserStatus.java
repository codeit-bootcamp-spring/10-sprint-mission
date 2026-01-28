package com.sprint.mission.discodeit.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class UserStatus extends BaseEntity{
    private UUID userID;
    private Instant lastConnectedAt;

    public boolean isOnline(){
        return (Duration.between(lastConnectedAt, Instant.now()).getSeconds() < 300);
    }



}
