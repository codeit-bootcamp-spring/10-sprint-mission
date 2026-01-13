package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Channel channel;
    private final User user;
    private final Long createdAt;
    private Long updatedAt;
    private String content;


    public Message(String content, Channel channel, User user) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.content = content;
        this.channel = channel;
        this.user = user;
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

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getMessageStatus() {
        return "메세지: " + content + ", 채널 아이디: " + channel.getId() + ", 유저 아이디: " + user.getId();
    }
}
