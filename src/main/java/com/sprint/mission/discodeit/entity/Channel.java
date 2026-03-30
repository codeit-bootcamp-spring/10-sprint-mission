package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type")
    private ChannelType type;

    public Channel(String name, String description, ChannelType type) {
        super();
        // 필드 초기화
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void updateChannelName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateChannelType(ChannelType channelType) {
        this.type = channelType;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + super.getId() + '\'' +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                ", channelName='" + name + '\'' +
                ", description=" + description +
                '}';
    }
}
