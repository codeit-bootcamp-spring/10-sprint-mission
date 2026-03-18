package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "read_statuses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_read_statuses_user_channel",
                        columnNames = {"user_id", "channel_id"}
                )
        }
)
public class ReadStatus extends BaseUpdatableEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "last_read_at", nullable = false)
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        assignUser(user);
        assignChannel(channel);
    }

    public void updateLastReadAt(Instant newLastReadAt) {
        if (newLastReadAt != null) {
            this.lastReadAt = newLastReadAt;
        }
        markUpdated();
    }

    public void assignUser(User user) {
        if (this.user == user) {
            return;
        }

        User previousUser = this.user;
        this.user = user;

        if (previousUser != null) {
            previousUser.removeReadStatus(this);
        }
        if (user != null && !user.getReadStatuses().contains(this)) {
            user.addReadStatus(this);
        }
    }

    public void assignChannel(Channel channel) {
        if (this.channel == channel) {
            return;
        }

        Channel previousChannel = this.channel;
        this.channel = channel;

        if (previousChannel != null) {
            previousChannel.removeReadStatus(this);
        }
        if (channel != null && !channel.getReadStatuses().contains(this)) {
            channel.addReadStatus(this);
        }
    }

    public void clearUser() {
        this.user = null;
    }

    public void clearChannel() {
        this.channel = null;
    }
}
