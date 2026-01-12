package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// User, Channel, Message 간 공통 필드, Getter, Setter가 존재해 추상 Class

public abstract class Base {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(){
        this.updatedAt = System.currentTimeMillis();
    }
    // Update
    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
