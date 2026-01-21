package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String username;
    private final List<Message> messages = new ArrayList<>();
    private final List<Channel> channels = new ArrayList<>();


    public User(String username) {
        super();
        this.username = username;
    }

    public void updateUsername(String username) {
        this.username = username;
        this.updateUpdatedAt();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        this.channels.remove(channel);
    }

    public String getUsername() { return username; }
    public List<Message> getMessages() { return messages; }
    public List<Channel> getChannels() { return channels; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
