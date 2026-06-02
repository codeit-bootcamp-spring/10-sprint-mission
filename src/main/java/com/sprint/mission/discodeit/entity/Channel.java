package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "channels")
public class Channel extends BaseUpdateEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ChannelType type;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;
    //
    @Column(name = "last_message_at") // 채널의 마지막 메시지 비정규화
    private Instant lastMessageAt;

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.lastMessageAt = null;
    }

    public void update(String newName, String newDescription) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
    }

    public void updateLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
