package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_statuses")
@Getter
@Setter
@RequiredArgsConstructor
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.now();
    }

    public void updateUser(User user) {
        this.user = user;
        if (user.getStatus() == null) {
            user.updateStatus(this);
        }
    }

    public void updateLastAccessedTime(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }


    public boolean isLoggedIn() {
        return Instant.now().isBefore(lastActiveAt.plus(5, ChronoUnit.MINUTES));
    }

}
