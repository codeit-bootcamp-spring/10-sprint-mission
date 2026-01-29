package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {
    private String channelName;
    private ChannelType channelType;
    private List<UUID> joinedUserIds;

    public Channel(String channelName, ChannelType channelType) {
        super();
        // 필드 초기화
        this.channelName = channelName;
        this.channelType = channelType;
        this.joinedUserIds = new ArrayList<>();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        setUpdatedAt();
    }

    public void updateChannelType(ChannelType channelType) {
        this.channelType = channelType;
        setUpdatedAt();
    }

    public void addUser(UUID userId) {
        if (joinedUserIds.stream().noneMatch(id -> id.equals(userId))) {
            joinedUserIds.add(userId);
        }
    }

    public void removeUser(UUID userId) {
        joinedUserIds.removeIf(user -> user.equals(userId));
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", joinedUsers=" + joinedUserIds +
                '}';
    }
}
