package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends CommonEntity{
    private String channelName;
    private ChannelType channelType;
    private String description;
    private final List<Message> messages = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public Channel(String channelName, ChannelType channelType, String description) {
        this.channelName = channelName;
        this.channelType = channelType;
        this.description = description;
    }

    public String getChannelName() {
        return channelName;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public String getDescription() {
        return description;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updateAt = System.currentTimeMillis();
    }

    public void updateChannelType() {
        channelType = channelType.switchType();
        this.updateAt = System.currentTimeMillis();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updateAt = System.currentTimeMillis();
    }

    public void addMessage(Message message) {
        messages.add(message);
        this.updateAt = System.currentTimeMillis();
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        this.updateAt = System.currentTimeMillis();
    }

    public void addUser(User user) {
        users.add(user);
        this.updateAt = System.currentTimeMillis();
    }

    public void removeUser(User user) {
        users.remove(user);
        this.updateAt = System.currentTimeMillis();
    }

    public void updateUser(User user) {
        users.set(users.indexOf(user), user);
        this.updateAt = System.currentTimeMillis();
    }
}
