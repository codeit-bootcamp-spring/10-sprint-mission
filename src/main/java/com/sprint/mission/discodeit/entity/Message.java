package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private User sender;
    private UUID channelId;
    private String content;

    public Message(User user, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.channelId = channelId;
        this.content = content;
        this.addUser(user);
    }

    public void addUser(User user) {
        this.sender = user;
        if (!user.getMessages().contains(this)) {
            user.addMessages(this);
        }
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

    public User getSender() {
        return sender;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getContent() {
        return content;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateSenderId(User sender) {
        this.sender = sender;
    }

    public void updateChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "보낸 사람 : " + sender.getName() + ", 내용 : " + content;
    }
}
