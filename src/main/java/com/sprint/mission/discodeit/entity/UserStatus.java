package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Setter;

@Getter
public class UserStatus extends BaseEntity {

    private final UUID userID;
    @Setter
    private Instant lastActiveAt;
    private boolean online;

    public UserStatus(UUID userID) {
        this.userID = userID;
        this.lastActiveAt = Instant.now();
    }

    public boolean isOnline() {
        return (Duration.between(lastActiveAt, Instant.now()).getSeconds() < 300);
    }

    public void update(Instant lastActivateAt) {
        this.lastActiveAt = lastActivateAt;
    }

}
