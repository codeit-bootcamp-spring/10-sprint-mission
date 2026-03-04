package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class UserStatus extends BaseUpdatableEntity {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Instant lastLoginAt;

    public UserStatus(User user, Instant lastLoginAt) {
        this.user = user;
        this.lastLoginAt = lastLoginAt;
    }

    public void updateLastLoginAt() {
        lastLoginAt = Instant.now();
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

    @Override
    public String toString() {
        return "UserStatus{" +
                "userId=" + user.getId() +
                ", lastLoginAt=" + lastLoginAt +
                '}';
    }
}
