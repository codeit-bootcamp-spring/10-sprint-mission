package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Channel extends BaseEntity{
    private String channelName;         // 채널 이름 (변경 가능)
    private User user;                  // 채널 소유자 (변경 불가능)
    private ArrayList<User> users;      // 채널 참가자 (변경 가능)
    private ChannelType type;           // CHAT, VOICE (변경 불가능)

    public Channel(String channelName, User user, ChannelType channelType) {
        this.users = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.user = user;
        this.users.add(user);
        this.type = channelType;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }
    public Long getCreatedAt() {
        return createdAt;
    }
    public Long getUpdatedAt() {
        return updatedAt;
    }
    public String getChannelName() {
        return channelName;
    }
    public User getuser() {
        return user;
    }
    public ArrayList<User> getUsers() {
        return users;
    }
    public ChannelType getChannelType() {
        return type;
    }
}