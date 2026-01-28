package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@ToString
public class UserStatus extends BaseEntity{
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    // 5분 이내 접속 여부 판단
    public boolean isOnline(){
        return lastActiveAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }

    // 마지막 접속시간 저장
    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        this.updated();
    }
}
