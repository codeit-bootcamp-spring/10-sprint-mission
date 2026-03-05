package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "USER_STATUSES")
public class UserStatus extends BaseEntity {

    @LastModifiedDate
    private Instant updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus(User user) {
//        super();//id,createdAt 값 삽입(BseEntity)

        this.user = user;
        this.lastActiveAt = Instant.now();
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
