package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseDomain{
    private UUID userId;
    private boolean isOnline;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
    }

    public boolean checkOnline() {
        Instant nowTime = Instant.now();
        if (this.updatedAt.isBefore(nowTime.minusSeconds(300))) {
            isOnline = false;
            return isOnline;
        }
        isOnline = true;
        return isOnline;
    }

    public void updateOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
