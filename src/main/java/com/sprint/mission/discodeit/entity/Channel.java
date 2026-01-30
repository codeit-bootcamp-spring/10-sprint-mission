package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class Channel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String channelName;
    private final List<User> channelUsers = new ArrayList<>();

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        touch();
    }

    public void addChannelUser(User user) {
        channelUsers.add(user);
        touch();
    }

    public void removeChannelUser(User user) {
        channelUsers.remove(user);
        touch();
    }

    public boolean hasChannelUser(User user) {
        return channelUsers.contains(user);
    }

    public boolean hasUserId(UUID userId) {
        return channelUsers.stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}