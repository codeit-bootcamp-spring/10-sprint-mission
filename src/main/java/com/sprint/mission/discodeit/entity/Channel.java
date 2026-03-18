package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ChannelType type;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadStatus> readStatuses = new ArrayList<>();

    public Channel(
            String name,
            String description,
            ChannelType type
    ) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public static Channel buildPublic(
            String name,
            String description
    ) {
        return new Channel(
                name,
                description,
                ChannelType.PUBLIC
        );
    }

    public static Channel buildPrivate() {
        return new Channel(
                null,
                null,
                ChannelType.PRIVATE
        );
    }

    public boolean isPublic() {
        return type.isPublic();
    }

    public boolean isPrivate() {
        return type.isPrivate();
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        if (lastMessageAt == null) {
            return;
        }
        if (this.lastMessageAt == null || this.lastMessageAt.isBefore(lastMessageAt)) {
            this.lastMessageAt = lastMessageAt;
            markUpdated();
        }
    }

    public void updateInfo(String name, String description) {
        Optional.ofNullable(name)
                .ifPresent(value -> this.name = value);
        Optional.ofNullable(description)
                .ifPresent(value -> this.description = value);

        markUpdated();
    }

    public void addMessage(Message message) {
        if (message == null) {
            return;
        }
        if (!messages.contains(message)) {
            messages.add(message);
        }
        if (message.getChannel() != this) {
            message.assignChannel(this);
        }
    }

    public void removeMessage(Message message) {
        if (message == null) {
            return;
        }
        messages.remove(message);
        if (message.getChannel() == this) {
            message.clearChannel();
        }
    }

    public void addReadStatus(ReadStatus readStatus) {
        if (readStatus == null) {
            return;
        }
        if (!readStatuses.contains(readStatus)) {
            readStatuses.add(readStatus);
        }
        if (readStatus.getChannel() != this) {
            readStatus.assignChannel(this);
        }
    }

    public void removeReadStatus(ReadStatus readStatus) {
        if (readStatus == null) {
            return;
        }
        readStatuses.remove(readStatus);
        if (readStatus.getChannel() == this) {
            readStatus.clearChannel();
        }
    }
}
