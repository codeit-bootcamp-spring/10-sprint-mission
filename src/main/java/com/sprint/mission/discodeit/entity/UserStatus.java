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
    private boolean isActive;
    private Instant lastActiveAt;

    public UserStatus(UUID userId,boolean isActive, Instant lastActiveAt) {
        super();
        this.userId = userId;
        this.isActive = isActive;
        this.lastActiveAt = lastActiveAt;
    }

    // 5분 이내 접속 여부
    public boolean isOnline(){
        return this.isActive && this.lastActiveAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }

    // 유저 상태 설정
    public void updateStatus(boolean online) {
        this.isActive = online;
        this.lastActiveAt = Instant.now();
        this.updated();
    }

    // 마지막 접속시간 저장
    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        this.updated();
    }
}
