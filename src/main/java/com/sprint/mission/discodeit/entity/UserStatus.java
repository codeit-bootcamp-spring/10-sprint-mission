package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;
    private UserStatusType statusType;
    private Instant lastLoginAt;

    public UserStatus(UUID userId, Instant lastLoginAt) {
        this.userId = userId;
        this.lastLoginAt = lastLoginAt;
        this.statusType = UserStatusType.ONLINE;
    }

    public void updateLastLoginAt() {
        lastLoginAt = Instant.now();
        updatedAt = lastLoginAt;
    }

    public void updateStatusType(UserStatusType userStatusType) {
        this.statusType = userStatusType;
    }

    // 마지막 로그인 기준으로 온라인인지 계산
    public boolean isCurrentlyLoggedIn() {
        if (lastLoginAt == null) {
            return false;
        }
        // 최근 로그인이 5분 이내일 경우 true
        return lastLoginAt.plusSeconds(300)
                .isAfter(Instant.now());
    }

    public UserStatusType getEffectiveStatus() {
        if (isCurrentlyLoggedIn()) {
            return statusType;
        } else {
            return UserStatusType.OFFLINE;
        }
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "userId=" + userId +
                ", userStatus='" + statusType + '\'' +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}
