package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final UUID channelId;
    private final UUID userId;
    private final Long createdAt;
    private Long updatedAt;
    private String content;


    public Message(String content, UUID channelId, UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.content = content;
        this.channelId = channelId;
        this.userId = userId;
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
    }

    public String getMessageStatus() {
        return "메세지: " + content + ", 채널 아이디: " + channelId + ", 유저 아이디: " + userId;
    }
}
