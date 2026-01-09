package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private long createdAt;
    private long updatedAt;
    private final User user;
    private String text;

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public void updateText(String text) {
        this.text = text;
        this.updatedAt = System.currentTimeMillis();
    }

    public Message(User user, String text) {
        id = UUID.randomUUID();
        this.user = user;
        this.text = text;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
}
