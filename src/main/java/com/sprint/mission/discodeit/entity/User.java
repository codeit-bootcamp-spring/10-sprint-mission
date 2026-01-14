package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;

public class User extends DefaultEntity {
    private String userName;
    private RoleGroup groups;
    private Set<Channel> allowedChannels;

    public User(String userName) {
        this.userName = userName;
        groups = null;
        allowedChannels = new HashSet<>();
    }

    public String getUserName() {
        return userName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    public String toString() {
        return userName;
    }

    public Set<Channel> getAllowedChannels() {
        return allowedChannels;
    }

    public void addAllowedChannel(Channel channel) { //유저에 추가된다고 그룹에까지 추가되는거 아님,
        allowedChannels.add(channel);
        if(!channel.getAllowedUsers().contains(this)) {
            channel.addAllowedUser(this);
        }
    }

    public void removeAllowedChannel(Channel channel) {
        if (allowedChannels.contains(channel)) {
            allowedChannels.remove(channel);
            channel.removeAllowedUser(this);
        }
    }
}
