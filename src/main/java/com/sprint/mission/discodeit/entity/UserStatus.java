package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    @Setter
    private UUID userId;
    @Setter
    private String userStatus;
    private Instant lastLoginAt;

    public UserStatus(UUID userId, String userStatus, Instant lastLoginAt) {
        this.userId = userId;
        this.userStatus = userStatus;
        this.lastLoginAt = lastLoginAt;
    }

    public void setLastLoginAt() {
        lastLoginAt = Instant.now();
        updatedAt = lastLoginAt;
    }

    public boolean isCurrentlyLoggedIn() {
        if (lastLoginAt == null) {
            return false;
        }
        // 최근 로그인이 5분 이내일 경우 true
        return lastLoginAt.plusSeconds(300)
                .isAfter(Instant.now());
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "userId=" + userId +
                ", userStatus='" + userStatus + '\'' +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}
