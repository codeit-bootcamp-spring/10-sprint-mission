package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private ChannelType type;
    private String channelName;
    private String description;
    private final Set<UUID> userIds;

    public Channel(ChannelType type, String channelName, String description) {
        this.type = type;
        this.channelName = channelName;
        this.description = description;
        this.userIds = new HashSet<>();
    }

    public String getChannelName() {
        return channelName;
    }

    public ChannelType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void addUser(UUID userId) {
        this.userIds.add(userId);
    }

    public void deleteUser(UUID userId) {
        this.userIds.remove(userId);
    }

    public List<UUID> getUserIds() {
        return new ArrayList<>(userIds);
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        setUpdateAt();
    }

    public void updateDescription(String description) {
        this.description = description;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return channelName;
    }
}
