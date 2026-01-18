package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity{
    private ChannelType type;
    private String channelName;
    private String description;
    private final Set<User> users;

    public Channel(ChannelType type, String channelName, String description) {
        this.type = type;
        this.channelName = channelName;
        this.description = description;
        this.users = new HashSet<>();
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

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        setUpdateAt();
    }

    public void updateDescription(String description) {
        this.description = description;
        setUpdateAt();
    }

    public void join(User user) {
        this.users.add(user);
    }

    public void leave(User user) {
        this.users.remove(user);
    }

    @Override
    public String toString() {
        return "[" + channelName + "]";
    }
}
