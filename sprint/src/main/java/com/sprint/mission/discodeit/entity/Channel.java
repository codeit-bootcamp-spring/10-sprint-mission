package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel {
    private UUID id;
    private String title;
    private long createdAt;
    private long updatedAt;
    private String description;
    private boolean isDeleted;
    private final Map<UUID, User> users;
    private final Map<UUID, Message> messages;

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void updateTitle(String title) {
        this.title = title;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Channel(String title, String description) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.isDeleted = false;
        this.users = new HashMap<>();
        this.messages = new HashMap<>();
    }
}
