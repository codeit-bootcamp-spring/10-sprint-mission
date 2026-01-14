package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private String content;
    private final User user;
    private final Channel channel;
    private final Long createdAt;
    private Long updatedAt;

    public Message(String content, User user, Channel channel) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    public UUID getId() { return id; }
    public String getContent() { return content; }
    public User getUser() { return user; }
    public Channel getChannel() { return channel; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userName=" + (user != null ? user.getName() : "null") +
                ", channelName=" + (channel != null ? channel.getName() : "null") +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
