package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// User, Channel, Message 간 공통 필드, Getter, Setter가 존재해 추상 Class

public abstract class Base {
    private UUID id;
    private Long createdAt;
    Long updatedAt;

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

    // Update
    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
