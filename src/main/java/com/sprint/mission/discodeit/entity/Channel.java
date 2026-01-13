package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {

    private UUID id;
    private final long createdAt;
    private long updatedAt;

    private String name;
    private String email;
    private String username;
    private String description;

    public Channel(String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.name = name;
        this.description = description;
    }

    public void update(UUID id,String name,String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // getter들도 추가
}

