package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor
public class UserStatus extends BaseUpdatableEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Instant lastActiveAt;

    public UserStatus(User user, Instant lastActiveAt) {
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        this.lastActiveAt = newLastActiveAt;
        isCurrentlyLoggedIn();
    }

    // 마지막 로그인 기준으로 온라인인지 계산
    public boolean isCurrentlyLoggedIn() {
        if (lastActiveAt == null) {
            return false;
        }
        // 최근 로그인이 5분 이내일 경우 true
        return lastActiveAt.plusSeconds(300)
                .isAfter(Instant.now());
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "lastLoginAt=" + lastActiveAt +
                '}';
    }
}
