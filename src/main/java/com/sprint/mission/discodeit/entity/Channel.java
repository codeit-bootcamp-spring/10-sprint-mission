package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {
    private String channelName;         // 채널 이름 (변경 가능)
    private User user;                  // 채널 소유자 (변경 불가능)
    private List<User> members;         // 채널 참가자 (변경 가능)
    private ChannelType type;           // CHAT, VOICE (변경 불가능)

    private List<Message> messages;     // 채널에서 발송된 메시지 목록

    public Channel(String channelName, User user, ChannelType channelType) {
        this.members = new ArrayList<>();
        this.messages = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.user = user;
        this.type = channelType;

        this.members.add(user);
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
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

    public User getUser() {
        return user;
    }

    public List<User> getMembers() {
        return members;
    }

    public ChannelType getChannelType() {
        return type;
    }

    public List<Message> getMessages() {
        return messages;
    }
}