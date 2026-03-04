package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "READ_STATUSES",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_read_statuses_user_channel",
                columnNames = {"user_id", "channel_id"}
        )
)
@NoArgsConstructor
public class ReadStatus extends BaseUpdatableEntity {
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;

    @Column(nullable = false, updatable = true)
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lastReadAt = Instant.now();
    }

    public void updateLastReadAt(Instant newLastReadAt) {
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "user=" + user +
                ", channel=" + channel +
                ", lastReadAt=" + lastReadAt +
                '}';
    }
}
