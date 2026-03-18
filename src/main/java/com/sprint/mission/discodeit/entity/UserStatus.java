package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "user_statuses")
@Getter
public class UserStatus extends BaseUpdatableEntity {
    private static final long ONLINE_TIME_OUT_MS = 5 * 60_000;  // 5분

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus() {
        super();
        this.lastActiveAt = Instant.now();
    }

    public void updateUser(User user) {
        if (this.user == null) {
            this.user = user;
            user.updateStatus(this);
        }
    }

    public void updateLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public boolean isOnline() {
        return Duration.between(this.lastActiveAt, Instant.now()).toMillis() <= ONLINE_TIME_OUT_MS;
    }
}
