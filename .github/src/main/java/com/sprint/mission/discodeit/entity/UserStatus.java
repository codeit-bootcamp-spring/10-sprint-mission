package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.userId = userId;
    }

    // 5분 이내 접속 여부 판단 메소드
    public boolean isOnline() {
        // 현재 시간 - 5분(300초) 이후 접속했다면 true
        return updatedAt.isAfter(Instant.now().minusSeconds(300));
    }

    public void updateActiveTime() {
        this.updatedAt = Instant.now();
    }

}
