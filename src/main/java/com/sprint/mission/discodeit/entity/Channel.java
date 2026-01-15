package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity{
    private String channelName;
    private final Set<User> users;
    private final List<Message> messages;

    public Channel(String channelName) {
        this.channelName = channelName;
        this.users = new HashSet<>();
        this.messages = new ArrayList<>();
    }

    public String getChannelName() {
        return channelName;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void update(String channelName) {
        this.channelName = channelName;
        setUpdateAt();
    }

    public void join(User user) {
        this.users.add(user);
    }

    public void send(Message message) {
        this.messages.add(message);
    }

    public void leave(User user) {
        this.users.remove(user);
    }

    public void delete(Message message) {
        this.messages.remove(message);
    }

    @Override
    public String toString() {
        return "[" + channelName + "]";
    }
}
