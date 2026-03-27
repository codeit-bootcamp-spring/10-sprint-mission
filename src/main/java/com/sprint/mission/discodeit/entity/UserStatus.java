package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    @Column(nullable = false)
    private boolean online;


    public UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.now();
        this.online = isOnline();
    }

    public UserStatus() {

    }

    public boolean isOnline() {
        return (Duration.between(lastActiveAt, Instant.now()).getSeconds() < 300);
    }

    public void update(Instant lastActivateAt) {
        this.lastActiveAt = lastActivateAt;
    }

}
