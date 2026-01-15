package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {

    private String channelName;
    private final List<User> members = new ArrayList<>();

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        touch();
    }

    public void addMember(User user) {
        members.add(user);
        touch();
    }

    public void removeMember(User user) {
        members.remove(user);
        touch();
    }

    public boolean hasMember(User user) {
        return members.contains(user);
    }

    public String getChannelName() {
        return channelName;
    }

    public List<User> getMember() {
        return members;
    }
}
