package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {
    private String channelName;
    private String description;
    private ChannelType channelType;
    private List<UUID> joinedUserIds;

    public Channel(String channelName, String description, ChannelType channelType) {
        super();
        // 필드 초기화
        this.channelName = channelName;
        this.description = description;
        this.channelType = channelType;
        this.joinedUserIds = new ArrayList<>();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        setUpdatedAt();
    }

    public void updateDescription(String description) {
        this.description = description;
        setUpdatedAt();
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
        setUpdatedAt();
    }

    public void updateUser(UUID userId) {
        if (!joinedUserIds.contains(userId)) {
            joinedUserIds.add(userId);
            setUpdatedAt();
        }
    }

    public void removeUser(UUID userId) {
        joinedUserIds.remove(userId);
        setUpdatedAt();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", description=" + description +
                ", joinedUsers=" + joinedUserIds +
                '}';
    }
}
