package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * "userId"
     * 연결된 유저의 식별자.
     * User 객체가 BaseEntity로부터 상속받아 가지게 된 'id' 값을 그대로 복사해서 저장함.
     * 이 값을 통해 "누구의 온라인 상태인지"를 식별함.
     */
    private final UUID userId;
    private Instant lastOnlineAt;

    public UserStatus(UUID userId, Instant lastOnlineAt) {
        super();
        this.userId = userId;
        this.lastOnlineAt = lastOnlineAt;
    }

    public void update(Instant lastOnlineAt) {
        this.lastOnlineAt = lastOnlineAt;
        super.update();
    }

    public boolean isOnline() {
        if (getLastOnlineAt() == null) return false;
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        return getLastOnlineAt().isAfter(fiveMinutesAgo);
    }
}
