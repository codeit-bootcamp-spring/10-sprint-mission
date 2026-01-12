package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private final UUID userId;
    private final UUID channelId;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + getId() +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", content='" + content + '\'' +
                '}';
    }
    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}
