package com.sprint.mission.discodeit.userstatus.entity;

import com.sprint.mission.discodeit.common.CommonEntity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends CommonEntity {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private Instant lastOnlineAt;
    private final int loginLimitSeconds = 60 * 5;

    public UserStatus(UUID userId) {
        this.userId = userId;
        lastOnlineAt = Instant.now();
    }

    public void updateLastOnlineAt() {
        lastOnlineAt = Instant.now();
    }

    public boolean isOnline() {
        return lastOnlineAt.isAfter(Instant.now().minusSeconds(loginLimitSeconds));
    }
}
