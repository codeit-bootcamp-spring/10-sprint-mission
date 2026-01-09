package com.sprint.mission.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
    }

    public void updateName(String name) {
        if (!name.isEmpty()) {
            this.name = name;
            touch();
        }
    }

    private void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}
