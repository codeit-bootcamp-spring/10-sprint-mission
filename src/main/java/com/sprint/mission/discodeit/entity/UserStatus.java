package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseDomain{
    private UUID userId;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
    }

    public boolean isOnline() {
        Instant nowTime = Instant.now();
        if (this.updatedAt.isBefore(nowTime.minusSeconds(300))) {
            return false;
        }
        return true;
    }
}
