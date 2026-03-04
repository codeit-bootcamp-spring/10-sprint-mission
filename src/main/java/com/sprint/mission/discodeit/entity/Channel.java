package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class Channel extends BaseUpdatableEntity {
    private String channelName;
    private String description;
    private ChannelType channelType;

    public Channel(String channelName, String description, ChannelType channelType) {
        super();
        // 필드 초기화
        this.channelName = channelName;
        this.description = description;
        this.channelType = channelType;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + super.getId() + '\'' +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                ", channelName='" + channelName + '\'' +
                ", description=" + description +
                '}';
    }
}
