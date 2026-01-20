package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    protected UUID id;
    protected Long createdAt;
    protected Long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
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
    void updateUpdatedAt() {
    }
}