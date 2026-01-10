package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private String channelName;
    private List<User> joinedUsers;

    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        // 필드 초기화
        this.channelName = channelName;
        this.joinedUsers = new ArrayList<>();
    }

    public UUID getChannelId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public List<User> getJoinedUsers() {
        return joinedUsers;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addUser(User user) {
        joinedUsers.add(user);
    }

    public void removeUser(UUID userId) {
        joinedUsers.removeIf(user -> user.getUserId().equals(userId));
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", joinedUsers=" + joinedUsers +
                '}';
    }
}
