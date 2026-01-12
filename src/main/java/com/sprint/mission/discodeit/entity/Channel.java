package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends CommonEntity{
    private String channelName;
    private ChannelType channelType;
    private String description;
    private List<Message> messages;
    private List<User> users;

    public Channel(String channelName, ChannelType channelType, String description) {
        this.channelName = channelName;
        this.channelType = channelType;
        this.description = description;
        this.messages = new ArrayList<>();
        this.users = new ArrayList<>();
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

    public void updateMessages(List<Message> messages) {
        this.messages = messages;
        this.updateAt = System.currentTimeMillis();
    }

    public void updateUsers(List<User> users) {
        this.users = users;
        this.updateAt = System.currentTimeMillis();
    }
}
