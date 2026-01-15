package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {
    private String username;
    private final List<Message> messages = new ArrayList<>();
    private final List<Channel> channels = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public List<Message> getMessages() { return messages; }
    public List<Channel> getChannels() { return channels; }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + getId() + ", username='" + username + '\'' + '}';
    }

    public void updateUsername(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }
    public void removeChannel(Channel channel) {
        this.channels.remove(channel);
    }
}
